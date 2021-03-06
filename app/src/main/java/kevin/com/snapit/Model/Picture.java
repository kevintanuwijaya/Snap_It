/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package kevin.com.snapit.Model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.annotations.EntireEncrypted;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.Indexes;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

import java.util.Date;

/**
 * Definition of ObjectType Picture.
 *
 * @since 2021-07-12
 */
@PrimaryKeys({"users_email", "users_picturePath"})
@Indexes({"users_email:users_email", "users_picturePath:users_picturePath"})
public final class Picture extends CloudDBZoneObject {
    private String users_email;

    private String users_picturePath;

    public Picture() {
        super(Picture.class);
    }

    public void setUsers_email(String users_email) {
        this.users_email = users_email;
    }

    public String getUsers_email() {
        return users_email;
    }

    public void setUsers_picturePath(String users_picturePath) {
        this.users_picturePath = users_picturePath;
    }

    public String getUsers_picturePath() {
        return users_picturePath;
    }

}
