package com.fairfareindia.utils

object Constants {


    var SHARED_PREFERENCE_LOGIN_TOKEN = "token"
    var SHARED_PREFERENCE_ISLOGIN = "isLogin"
    var SHARED_PREFERENCE_LAT = "Lat"
    var SHARED_PREFERENCE_LONG = "Long"
    var SHARED_PREFERENCE_PICKUP_AITPORT = "Airport"


    var SHARED_PREFERENCE_LOGIN_ID = "id"
    var SHARED_PREFERENCE_USER_REWARD = "reward"
    var SHARED_PREFERENCE_LOGIN_NAME = "name"
    var SHARED_PREFERENCE_LOGIN_EMAIL = "email"
    var SHARED_PREFERENCE_LOGIN_PHONENO = "phoneNo"
    var SHARED_PREFERENCE_LOGIN_GENDER = "gender"
    var SHARED_PREFERENCE_LOGIN_PROFESTION = "profession"
    var SHARED_PREFERENCE_LOGIN_DOB = "dob"
    var SHARED_PREFERENCE_LOGIN_PROFILEPICK = "photo"
    var SHARED_PREFERENCE_LOGIN_LOCATION = "location"
    var SHARED_PREFERENCE_LOGIN_DEVICEID = "deviceId"
    var SHARED_PREFERENCE_CITY_ID = "city_id"


    var SHARED_PREFERENCE_CLat = "lat"
    var SHARED_PREFERENCE_CLong = "lang"

    // ILOMADEV:- This flag must be false to run iloma optimise code
    var IS_OLD_PICK_UP_CODE = false


    var vehicleImageClick = 1
    var meterImageClick = 2
    var driverImageClick = 3
    var badgeImageClick = 4

    // PERMIT TYPE
    var TYPE_INTERCITY = "Intercity"
    var TYPE_LOCAL = "Local"

    // BOOKING STATUS
    var BOOKING_PENDING = "Pending"
    var BOOKING_SCHEDULED = "Scheduled"
    var BOOKING_ACTIVE = "Active-Ride"
    var BOOKING_COMPLETED = "Completed"
    var BOOKING_CANCELLED = "Cancelled"

    // INTERCITY WAY FLAG
    var ONE_WAY_FLAG = "OneWay"
    var BOTH_WAY_FLAG = "BothWay"


    var REQUEST_TIMEOUT: Int = 1000 * 60

    //RazorPay Key
    //Test
    var RAZOR_PAY_KEY = "rzp_test_W9rG6mEpxnV17V"

    //Live
    // var RAZOR_PAY_KEY = "rzp_test_W9rG6mEpxnV17V";
}