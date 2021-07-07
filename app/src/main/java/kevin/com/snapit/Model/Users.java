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
 * Definition of ObjectType Users.
 *
 * @since 2021-07-07
 */
@PrimaryKeys({"users_email"})
@Indexes({"users_email:users_email"})
public final class Users extends CloudDBZoneObject {
    private String users_email;

    @NotNull
    @DefaultValue(stringValue = "person")
    private String users_name;

    public Users() {
        super(Users.class);
        this.users_name = "person";
    }

    public void setUsers_email(String users_email) {
        this.users_email = users_email;
    }

    public String getUsers_email() {
        return users_email;
    }

    public void setUsers_name(String users_name) {
        this.users_name = users_name;
    }

    public String getUsers_name() {
        return users_name;
    }

}
