package com.neterbox.qb;

import android.os.Bundle;
import android.util.Log;

import com.neterbox.App;
import com.neterbox.qb.callback.QbEntityCallbackTwoTypeWrapper;
import com.neterbox.qb.callback.QbEntityCallbackWrapper;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChatHelper {
    private static final String TAG = ChatHelper.class.getSimpleName();

    public static final int DIALOG_ITEMS_PER_PAGE = 100;
    public static final int CHAT_HISTORY_ITEMS_PER_PAGE = 50;
    private static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";

    private static ChatHelper instance;

    private QBChatService qbChatService;

    public static synchronized ChatHelper getInstance() {
        if (instance == null) {

            QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
            QBChatService.setDebugEnabled(true);
            QBChatService.setDefaultPacketReplyTimeout(30000);
            QBChatService.setDefaultConnectionTimeout(30000);
            QBChatService.setConfigurationBuilder(buildChatConfigs());
            instance = new ChatHelper();
        }
        return instance;
    }

    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
    }

    public static QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }

    private ChatHelper() {
        qbChatService = QBChatService.getInstance();
        qbChatService.setUseStreamManagement(true);
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        try {
            QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();
//            SampleConfigs sampleConfigs = null;
            SampleConfigs sampleConfigs = App.getSampleConfigs();;

            if (sampleConfigs != null) {
                int port = sampleConfigs.getChatPort();
                int socketTimeout = sampleConfigs.getChatSocketTimeout();
                boolean useTls = sampleConfigs.isUseTls();
                boolean keepAlive = sampleConfigs.isKeepAlive();
                boolean autoJoinEnabled = sampleConfigs.isAutoJoinEnabled();
                boolean autoMarkDelivered = sampleConfigs.isAutoMarkDelivered();
                boolean reconnectionAllowed = sampleConfigs.isReconnectionAllowed();
                boolean allowListenNetwork = sampleConfigs.isAllowListenNetwork();

                if (port != 0) {
                    configurationBuilder.setPort(port);
                }
                configurationBuilder.setSocketTimeout(socketTimeout);
                configurationBuilder.setUseTls(useTls);
                configurationBuilder.setKeepAlive(keepAlive);
                configurationBuilder.setAutojoinEnabled(autoJoinEnabled);
                configurationBuilder.setAutoMarkDelivered(autoMarkDelivered);
                configurationBuilder.setReconnectionAllowed(reconnectionAllowed);
                configurationBuilder.setAllowListenNetwork(allowListenNetwork);
            }

            return configurationBuilder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void addConnectionListener(ConnectionListener listener) {
        qbChatService.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        qbChatService.removeConnectionListener(listener);
    }

    public void login(final QBUser user, final QBEntityCallback<Void> callback) {
        try {
            // Create REST API session on QuickBlox
            QBUsers.signIn(user).performAsync(new QbEntityCallbackTwoTypeWrapper<QBUser, Void>(callback) {
                @Override
                public void onSuccess(QBUser qbUser, Bundle args) {
                    user.setId(qbUser.getId());
                    user.setFullName(qbUser.getFullName());
                    loginToChat(user, new QbEntityCallbackWrapper<>(callback));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loginToChat(final QBUser user, final QBEntityCallback<Void> callback) {
        try {
            if (qbChatService.isLoggedIn()) {
                callback.onSuccess(null, null);
                return;
            }

            qbChatService.login(user, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void join(QBChatDialog chatDialog, final QBEntityCallback<Void> callback) {
        try {
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);

            chatDialog.join(history, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void leaveChatDialog(QBChatDialog chatDialog) throws XMPPException, SmackException.NotConnectedException {
        chatDialog.leave();
    }

    public void destroy() {
        qbChatService.destroy();
    }

    public void createDialogWithSelectedUsers(final List<QBUser> users,
                                              final QBEntityCallback<QBChatDialog> callback) {

        QBRestChatService.createChatDialog(QbDialogUtils.createDialog(users)).performAsync(
                new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        QbDialogHolder.getInstance().addDialog(dialog);
                        QbUsersHolder.getInstance().putUsers(users);
                        super.onSuccess(dialog, args);
                    }
                });
    }

    public void deleteDialogs(Collection<QBChatDialog> dialogs, final QBEntityCallback<ArrayList<String>> callback) {
        try {
            StringifyArrayList<String> dialogsIds = new StringifyArrayList<>();
            for (QBChatDialog dialog : dialogs) {
                dialogsIds.add(dialog.getDialogId());
            }

            QBRestChatService.deleteDialogs(dialogsIds, false, null).performAsync(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteDialog(QBChatDialog qbDialog, QBEntityCallback<Void> callback) {
        try {
            if (qbDialog.getType() == QBDialogType.PUBLIC_GROUP) {

//            Toast.makeText()
//            Toaster.shortToast(R.string.public_group_chat_cannot_be_deleted);
            } else {
                QBRestChatService.deleteDialog(qbDialog.getDialogId(), false)
                        .performAsync(new QbEntityCallbackWrapper<Void>(callback));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void exitFromDialog(QBChatDialog qbDialog, QBEntityCallback<QBChatDialog> callback) {
        try {
            try {
                leaveChatDialog(qbDialog);
            } catch (XMPPException | SmackException.NotConnectedException e) {
                callback.onError(new QBResponseException(e.getMessage()));
            }

            QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
            qbRequestBuilder.removeUsers(QBChatService.getInstance().getUser().getId());

            QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateDialogUsers(QBChatDialog qbDialog,
                                  final List<QBUser> newQbDialogUsersList,
                                  QBEntityCallback<QBChatDialog> callback, String group_name, String groupPhoto) {
        try {
            List<QBUser> addedUsers = QbDialogUtils.getAddedUsers(qbDialog, newQbDialogUsersList);
            List<QBUser> removedUsers = QbDialogUtils.getRemovedUsers(qbDialog, newQbDialogUsersList);

            QbDialogUtils.logDialogUsers(qbDialog);
            QbDialogUtils.logUsers(addedUsers);
            Log.w(TAG, "=======================");
            QbDialogUtils.logUsers(removedUsers);

            QBDialogRequestBuilder qbRequestBuilder = new QBDialogRequestBuilder();
            if (!addedUsers.isEmpty()) {
                qbRequestBuilder.addUsers(addedUsers.toArray(new QBUser[addedUsers.size()]));
            }
            if (!removedUsers.isEmpty()) {
                qbRequestBuilder.removeUsers(removedUsers.toArray(new QBUser[removedUsers.size()]));
            }

//            qbDialog.setName(DialogUtils.createChatNameFromUserList(
//                    newQbDialogUsersList.toArray(new QBUser[newQbDialogUsersList.size()])));
            qbDialog.setName(group_name);
            if (groupPhoto != null)
                qbDialog.setPhoto(groupPhoto);

            QBRestChatService.updateGroupChatDialog(qbDialog, qbRequestBuilder).performAsync(
                    new QbEntityCallbackWrapper<QBChatDialog>(callback) {
                        @Override
                        public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                            QbUsersHolder.getInstance().putUsers(newQbDialogUsersList);
                            QbDialogUtils.logDialogUsers(qbDialog);
                            super.onSuccess(qbDialog, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadChatHistory(QBChatDialog dialog, int skipPagination,
                                final QBEntityCallback<ArrayList<QBChatMessage>> callback) {

        try {
            QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
            customObjectRequestBuilder.setSkip(skipPagination);
            customObjectRequestBuilder.setLimit(CHAT_HISTORY_ITEMS_PER_PAGE);
            customObjectRequestBuilder.sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);

            QBRestChatService.getDialogMessages(dialog, customObjectRequestBuilder).performAsync(
                    new QbEntityCallbackWrapper<ArrayList<QBChatMessage>>(callback) {
                        @Override
                        public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                            Set<Integer> userIds = new HashSet<>();
                            for (QBChatMessage message : qbChatMessages) {
                                userIds.add(message.getSenderId());
                            }

                            if (!userIds.isEmpty()) {
                                getUsersFromMessages(qbChatMessages, userIds, callback);
                            } else {
                                callback.onSuccess(qbChatMessages, bundle);
                            }
                            // Not calling super.onSuccess() because
                            // we're want to load chat users before triggering the callback
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getDialogs(QBRequestGetBuilder customObjectRequestBuilder, final QBEntityCallback<ArrayList<QBChatDialog>> callback) {
        customObjectRequestBuilder.setLimit(DIALOG_ITEMS_PER_PAGE);

        try {
            QBRestChatService.getChatDialogs(null, customObjectRequestBuilder).performAsync(
                    new QbEntityCallbackWrapper<ArrayList<QBChatDialog>>(callback) {
                        @Override
                        public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle args) {
                            Iterator<QBChatDialog> dialogIterator = dialogs.iterator();
                            while (dialogIterator.hasNext()) {
                                QBChatDialog dialog = dialogIterator.next();
                                if (dialog.getType() == QBDialogType.PUBLIC_GROUP) {
                                    dialogIterator.remove();
                                }
                            }

                            getUsersFromDialogs(dialogs, callback);
                            // Not calling super.onSuccess() because
                            // we want to load chat users before triggering callback
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getDialogById(String dialogId, final QBEntityCallback<QBChatDialog> callback) {
        QBRestChatService.getChatDialogById(dialogId).performAsync(callback);
    }

    public void getUsersFromDialog(QBChatDialog dialog,
                                   final QBEntityCallback<ArrayList<QBUser>> callback) {

        try {
            List<Integer> userIds = dialog.getOccupants();

            final ArrayList<QBUser> users = new ArrayList<>(userIds.size());
            for (Integer id : userIds) {
                users.add(QbUsersHolder.getInstance().getUserById(id));
            }

            // If we already have all users in memory
            // there is no need to make REST requests to QB
            if (userIds.size() == users.size()) {
                callback.onSuccess(users, null);
                return;
            }

            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);
            QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                    new QbEntityCallbackWrapper<ArrayList<QBUser>>(callback) {
                        @Override
                        public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                            QbUsersHolder.getInstance().putUsers(qbUsers);
                            callback.onSuccess(qbUsers, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadFileAsAttachment(File file, QBEntityCallback<QBAttachment> callback) {
        loadFileAsAttachment(file, callback, null);
    }


    public void loadFileAsAttachment(File file, QBEntityCallback<QBAttachment> callback,
                                     QBProgressCallback progressCallback) {

        try {
            QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl());
                            callback.onSuccess(attachment, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadFileAsAttachmentDocument(File file, QBEntityCallback<QBAttachment> callback,
                                             QBProgressCallback progressCallback) {

        try {
            QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBAttachment attachment = new QBAttachment(QBAttachment.CONTENT_TYPE_KEY);
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl());
                            attachment.setName(qbFile.getName());
                            callback.onSuccess(attachment, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadFileAsAttachmentAudio(File file, QBEntityCallback<QBAttachment> callback,
                                          QBProgressCallback progressCallback) {
        try {
            QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBAttachment attachment = new QBAttachment(QBAttachment.AUDIO_TYPE);
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl());
                            callback.onSuccess(attachment, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*TODO For Video Attachment*/
    public void loadFileAsAttachmentVideo(File file, QBEntityCallback<QBAttachment> callback,
                                          QBProgressCallback progressCallback) {

        try {
            QBContent.uploadFileTask(file, true, null, progressCallback).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<QBFile, QBAttachment>(callback) {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBAttachment attachment = new QBAttachment(QBAttachment.VIDEO_TYPE);
                            attachment.setId(qbFile.getId().toString());
                            attachment.setUrl(qbFile.getPublicUrl());
                            callback.onSuccess(attachment, bundle);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getUsersFromDialogs(final ArrayList<QBChatDialog> dialogs,
                                     final QBEntityCallback<ArrayList<QBChatDialog>> callback) {

        try {
            List<Integer> userIds = new ArrayList<>();
            for (QBChatDialog dialog : dialogs) {
                userIds.addAll(dialog.getOccupants());
                userIds.add(dialog.getLastMessageUserId());
            }

            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);

            QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<ArrayList<QBUser>, ArrayList<QBChatDialog>>(callback) {
                        @Override
                        public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                            QbUsersHolder.getInstance().putUsers(users);
                            callback.onSuccess(dialogs, params);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getUsersFromMessages(final ArrayList<QBChatMessage> messages,
                                      final Set<Integer> userIds,
                                      final QBEntityCallback<ArrayList<QBChatMessage>> callback) {

        try {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(userIds.size(), 1);
            QBUsers.getUsersByIDs(userIds, requestBuilder).performAsync(
                    new QbEntityCallbackTwoTypeWrapper<ArrayList<QBUser>, ArrayList<QBChatMessage>>(callback) {
                        @Override
                        public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                            QbUsersHolder.getInstance().putUsers(users);
                            callback.onSuccess(messages, params);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

   }
}