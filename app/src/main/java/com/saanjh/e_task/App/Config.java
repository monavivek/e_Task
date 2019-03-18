package com.saanjh.e_task.App;

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray

    public static final int NOTIFICATION_ID = 100;

    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    private static String API_TEST_URL = "http://10.0.0.116/";

    private static String API_LIVE_URL = "http://demo.emanagelive.com:8080/eTask/";

    private static String API_LIVE_URLL = "http://builtinidea.com/eTask/";

    public static String MAIN_URL = API_LIVE_URLL ;

  //  public static String MAIN_URL = API_TEST_URL + "mysqli_api/";

    public static String URL_LOGIN = "taskmanager_login.php";

    public static String URL_FORGOT = "forgot_password.php";

    public static String URL_RESET_PASSWORD = "password_change.php";

    public static String URL_UPDATE_QUERY = "update_query.php";

    public static String URL_IMAGES = "uploads/";

  //  public static String URL_IMAGES = "";

    public static String URL_EMPLOYEE_LIST = "taskmanager_employee_list.php";

    public static String URL_UPDATE_ASSIGN_TASK = "update_assigntask.php";

    public static String URL_TASK_ASSIGN = "task_assign.php";

    public static String URL_ASSIGN_TASK_LIST = "taskmanager_assigntask_list.php";

    public static String URL_TASK_LIST = "taskmanager_task_list.php";

    public static String URL_INSERT_QUERY = "insert_query.php";

    public static String URL_INSERT_QUERY_STATUS= "insert_query_status.php";

    public static String URL_NOTIFICATION_LIST = "taskmanager_notification_list.php";

    public static String URL_GCM_TOKEN_ALL = "gcm_token_all.php";

    public static String URL_GCM_ANDROID_SINGLE = "test.php";

    public static String URL_GCM_TASK_REPLY = "gcm_android_taskreply.php";

    public static String URL_UPLOAD_IMAGE_FILE = "upload_image_file.php";

    public static String URL_DELETE_TASK = "delete_task.php";

    public static String URL_UPDATETASK_LIST = "update_task_seen_list.php";

    public static String URL_UPDATE_SEND_DATA= "update_send_data_new.php";

    public static String URL_UPDATE_STATUS= "update_status.php";

    public static String URL_EXCEPTION_HANDLER= "exception.php";


}
