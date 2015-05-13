package com.ozm.rocks.data.api.response;

import com.google.gson.annotations.SerializedName;

public final class Order {
    public final long id;
    public final String coupon;
    @SerializedName("description_permalink")
    public final String descriptionPermalink;
    public final boolean activated;
    @SerializedName("offer_id")
    public final long offerId;
    public final String title;
    @SerializedName("deal_id")
    public final long dealId;
    public final boolean returned;
    @SerializedName("valid_to")
    public final String validTo;
    @SerializedName("need_security")
    public final boolean needSecurity;
    @SerializedName("need_voucher?")
    public final boolean needVoucher;
    @SerializedName("used_at")
    public final String usedAt;

    public Order(long id, String coupon, String descriptionPermalink, boolean activated,
                 long offerId, String title, long dealId,
                 boolean returned, String validTo, boolean needSecurity,
                 boolean needVoucher, String usedAt) {
        this.id = id;
        this.coupon = coupon;
        this.descriptionPermalink = descriptionPermalink;
        this.activated = activated;
        this.offerId = offerId;
        this.title = title;
        this.dealId = dealId;
        this.returned = returned;
        this.validTo = validTo;
        this.needSecurity = needSecurity;
        this.needVoucher = needVoucher;
        this.usedAt = usedAt;
    }
}
