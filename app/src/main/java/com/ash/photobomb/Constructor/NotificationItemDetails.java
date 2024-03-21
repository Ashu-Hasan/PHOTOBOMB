package com.ash.photobomb.Constructor;

import com.ash.photobomb.API_Model_Classes.NotificationModel;

import java.util.ArrayList;

public class NotificationItemDetails {
    int itemImageId;

    NotificationModel notificationItem;

    public NotificationItemDetails(int itemImageId, NotificationModel notificationItem) {
        this.itemImageId = itemImageId;
        this.notificationItem = notificationItem;
    }

    public NotificationModel getNotificationItem() {
        return notificationItem;
    }

    public void setNotificationItem(NotificationModel notificationItem) {
        this.notificationItem = notificationItem;
    }

    public int getItemImageId() {
        return itemImageId;
    }

    public void setItemImageId(int itemImageId) {
        this.itemImageId = itemImageId;
    }

}
