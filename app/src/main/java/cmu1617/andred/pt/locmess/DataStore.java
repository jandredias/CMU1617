package cmu1617.andred.pt.locmess;

/**
 * Created by andre on 07/01/17.
 */

public interface DataStore {

    String SHARED_PREFERENCES = "shared";

    String IMAGES_DIRECTORY = "images";

    String SQL_SIGN_IN_PAGER = "signin_pager";

    String[] SQL_SIGN_IN_PAGER_COLUMNS = {
            "page_title",
            "page_image"
    };


    String SQL_CREATE_SIGN_IN_PAGER =
            "CREATE TABLE " + SQL_SIGN_IN_PAGER + " (" +
            "page_title VARCHAR(1024) NOT NULL," +
            "page_image VARCHAR(255) NOT NULL)";

    String SQL_POPULATE_SIGN_IN_PAGER =
            "INSERT INTO " + SQL_SIGN_IN_PAGER + " (page_title,page_image) VALUES " +
                    "('Page1', 'image1')," +
                    "('Page2','image2')," +
                    "('Page3','image3')";

    String SQL_DELETE_SIGN_IN_PAGER =
            "DROP TABLE IF EXISTS " + SQL_SIGN_IN_PAGER;

    String SQL_CHATS_MESSAGES = "chats_messages";
    String SQL_CHATS = "chats";
    String SQL_USERS = "users";

    String SQL_USER_LOCAL_IN = "local_in";
    String SQL_QUESTIONS = "questions";
    String SQL_QUESTIONS_ACCEPTED = "questions_accepted";
    String SQL_QUESTIONS_LOCALS = "questions_locals";
    String SQL_CITIES = "cities";
//    String SQL_EXPERTISE = "expertise";
    String SQL_TIP = "tip";
    String SQL_NO_TIP = "no_tip";
    String SQL_PROFILE_UPDATE = "update_profile";
    String SQL_REVIEW_LOCAL = "review_local";
    String SQL_REVIEW_VISITOR = "review_visitor";
    String SQL_PROFILE_BECOME_LOCAL = "become_local";

    String SQL_FAQS_LOCAL = "faq_local";
    String SQL_FAQS_VISITOR = "faq_visitor";

    String[] SQL_REVIEW_LOCAL_COLUMNS = {
            "review_id",
            "user_id",  //receiver
            "visitor",
            "review"
    };

    String[] SQL_REVIEW_VISITOR_COLUMNS = {
            "review_id",
            "user_id", //receiver
            "local",
            "review"
    };

    String[] SQL_PROFILE_BECOME_LOCAL_COLUMNS = {
            "become",
            "city",
            "uploaded"
    };


    String[] SQL_PROFILE_UPDATE_COLUMNS = {
            "id",
            "field_name",
            "value",
            "uploaded"
    };


    String[] SQL_TIP_COLUMNS = {
            "chat_id",
            "tip",
            "rating",
            "review",
            "review_from_local",
            "paid",
            "uploaded"
    };

    String[] SQL_NO_TIP_COLUMNS = {
            "chat_id",
            "reasons",
            "uploaded"
    };

 /*   String[] SQL_EXPERTISE_COLUMNS ={
            "expertise_id",
            "expertise_name"
    };*/

    String[] SQL_CITIES_COLUMNS = {
            "city_id",
            "city_name",
            "country_name"
    };
    String[] SQL_QUESTIONS_COLUMNS = {
            "question_id",
            "city_id",
            "city_name",
            "country_name",
            "question_value",
            "question_date",
            "sent",
            "ROWID"
    };

    String[] SQL_QUESTIONS_ACCEPTED_COLUMNS = {
            "question_id",
            "local_id",
            "local_answer_id",
            "answered"
    };

    String[] SQL_CHATS_MESSAGES_COLUMNS = {
            "message_id",
            "chat_id",
            "type",
            "content",
            "mine",
            "message_date",
            "seen",
            "ROWID",
            "uploaded"
    };
    String[] SQL_CHATS_COLUMNS = {
            "chat_id",
            "question_id",
            "question_value",
            "city_name",
            "city_id",
            "country_name",
            "local_id",
            "local_name",
            "visitor_id",
            "visitor_name",
            "closed",
            "closedOnServer",
            "tipped"
    };
    String[] SQL_USERS_COLUMNS = {
            "user_id",                  //0
            "user_name",                //1
            "photo",                    //2
            "age",                      //3
            "user_from_city_name",      //4
            "user_from_city_id",        //5
            "user_from_country_name",   //6
            "sentence",                 //7
            "average_tip_received",     //8
            "average_tip_given",        //9
            "requests_visitor",         //10
            "reviews",                  //11
            "isLocal",                  //12
            "requests_local",           //13
            "rating",                   //14
            "timestamp",                //15
            "expertise"                 //16
    };

    String[] SQL_USER_LOCAL_IN_COLUMNS = {
            "user_id",
            "city_id"
    };
    String[] SQL_QUESTIONS_LOCALS_COLUMNS = {
            "question_id",
            "question_value",
            "user_id",
            "city_id",
            "city_name",
            "country_name",
            "question_date",
            "answered"
    };

    String[] SQL_FAQS_LOCAL_COLUMNS = {
            "question",
            "answer"
    };
    String[] SQL_FAQS_VISITOR_COLUMNS = {
            "question",
            "answer"
    };

    String SQL_CREATE_FAQS_LOCAL =
            "CREATE TABLE " + SQL_FAQS_LOCAL + " (" +
                    "question VARCHAR(255) NOT NULL," +
                    "answer VARCHAR(2048) NOT NULL)";
    String SQL_CREATE_FAQS_VISITOR =
            "CREATE TABLE " + SQL_FAQS_VISITOR + " (" +
                    "question VARCHAR(255) NOT NULL," +
                    "answer VARCHAR(2048) NOT NULL)";


    String SQL_POPULATE_FAQS_VISITOR =
            "INSERT INTO " + SQL_FAQS_VISITOR + " (question, answer) VALUES " +
                    "('How does SnapCity work?','As simple as it gets, just follow the steps:\n" +
                    "1. Download the APP. It’s free.\n" +
                    "2. Sign in and enter your profile details (optional). You’re ready chat with a Local!\n" +
                    "3. Choose the city you are visiting and ask whatever you want to know.\n" +
                    "4. The Locals receive your request and those who can help you send you their request\n" +
                    "5. Choose the one(s) you want and start chatting!\n" +
                    "6. During the chat, you can can get all the recommendations and advice to help you make smart travel decisions.\n" +
                    "7. In the end, thank them with a TIP ($$$) according to your satisfaction. You decide what to pay!')," +
                    "('Who are the Locals?','A SnapCity Local is that friend you always wanted to have everywhere you go, who can help you with awesome recommendations with an authentic local perspective.')," +
                    "('How to choose the ideal Local?','Is very easy to choose the ideal Local. After you receive the feedback from Locals that can help you with your question just browse their profiles and choose the ones that have the interests and areas of expertise that you are looking for.')," +
                    "('How long does a chat with a Local lasts?','There is no time limit. Between yourselves you decide when to maintain the chat active. When the service is no longer needed one of you ends the chat.')," +
                    "('How is the payment done?','The payment occurs once the chats ends. Before that there is no transfer of fees involved.')," +
                    "('In what language will my Local talk to me?','SnapCity default language is English. You can ask questions to the Locals community in other languages. Those who reply can assist you in that language.')," +
                    "('Why should I pay someone to give me information about the city I am visiting?','It´s very hard to cope with the excess of information and increasing demand to experience the unique and customized, it is getting harder and harder to filter what is relevant and select where to go and what to do when you travel. More and more travellers are seeking for a cultural immersion when traveling the world. SnapCity Locals are also travellers but within their own homes and thus do this homework for you while putting together an itinerary that is 100% customized suiting your needs, interests and making sure to take you to the most authentic and sometimes even traditional little places, guiding you on what and where to shop, teaching about local eating habbits, and showing what is truly authentic while keeping you up to date with the new and noteworthy. You will not have to worry about anything and certainly won’t get lost.')";
    String SQL_POPULATE_FAQS_LOCAL = "" +
            "INSERT INTO " + SQL_FAQS_LOCAL + " (question, answer) VALUES " +
            "('Who are the Locals?','A SnapCity Local is anyone who has an authentic local perspective from his city to share.')," +
            "('How does SnapCity work?','As simple as it gets, just follow the steps:\n" +
            "1. Download the APP. Yes, It’s free.\n" +
            "2. Sign in and enter your profile details. You’re ready to be a Local!\n" +
            "3. You receive chat requests from Visitors.  If you accept the request, the visitor can start chatting with you.\n" +
            "4. During the chat, you can help visitors with information and advice about your city. Share your knowledge and help them making smart travel decisions.\n" +
            "5. In the end, they thank you with a TIP ($$$) according to their satisfaction.')," +
            "('Is the App SnapCity free?','Yes, the app is free to download.')," +
            "('How do I make money out of this?','The revenue share is of 70% to you and 30% to SnapCity.')," +
            "('Do I have to pay taxes on my earnings?','Yes. As a Local you are responsible for determining the applicable tax law in your country related to the payment you receive. This can range from VAT to surcharges, sales taxes, goods and services taxes or personal/corporate income.')," +
            "('What does it take for me to become a SnapCity Local?','Are you passionate about your city? Social and communicative? Then you are on the right track! Sign in and try it out!')," +
            "('How do I become a Local?','Just activate your Local Host status on your profile and fill out the necessary information that is required.')," +
            "('How to boost your profile?','The more people know about yourself it gets easier to find common ground and generate empathy. Make sure you share something about you that summarizes who you are in a very simple and down to earth way. Share your interests and what you like about your city.Upload a good resolution foto of yourself (a clear and smily face is always welcoming). Reviews are very important. Ask visitors for reviews after chatting. Lastly, boosting but not sharing is the same as not boosting. Make sure to share your profile in social media channels and through your friends and family. Word of mouth still is a very powerful channel.')," +
            "('How do I cancel my Local profile?','If by any chance you don’t want to receive more requests from visitors you can simply deactivate your Local Host status on your profile. Your information as a Local will be kept and you can simply just turn it back on whenever you want.')," +
            "('How does the rate and review system work?','After the end of a specific chat, the visitor will automatically be complied to give a star rating (1 to 5). They can also leave a complementary review that will appear on your profile. After that you, the Local, receive a notification with that procedure information and can also leave a review that will appear on the visitor’s profile.')," +
            "('Why become a SnapCity Local?','Want a true complementary freelance activity that is fun and flexible? While chatting about what you know and love from wherever and whenever you are available to do so? Then being a Snapcity Local is  the gig for you!')," +
            "('How much does a Local get of the Tip?','The revenue share of The Tip is 70% for the Local and 30% for SnapCity.')," +
            "('If my city isn’t available what do I do?','If you cannot locate your city on our list of available cities go to our website and click on “become a local”, fill out that form and as soon your city will be available we will get in touch with you.')";
    String SQL_CREATE_USER_LOCAL_IN =
            "CREATE TABLE " + SQL_USER_LOCAL_IN + " (" +
                    "user_id VARCHAR(255) NOT NULL," +
                    "city_id VARCHAR(255) NOT NULL)";

    String SQL_CREATE_REVIEW_LOCAL =
            "CREATE TABLE " + SQL_REVIEW_LOCAL + " (" +
                    "review_id VARCHAR(255) NOT NULL," +
                    "user_id VARCHAR(255) NOT NULL," +
                    "visitor VARCHAR(255) NOT NULL," +
                    "review VARCHAR(1000))";

    String SQL_CREATE_REVIEW_VISITOR =
            "CREATE TABLE " + SQL_REVIEW_VISITOR + " (" +
                    "review_id VARCHAR(255) NOT NULL," +
                    "user_id VARCHAR(255) " +
                    "NOT NULL," +
                    "local VARCHAR(255) NOT NULL," +
                    "review VARCHAR(1000))";

    String SQL_CREATE_PROFILE_BECOME_LOCAL =
            "CREATE TABLE " + SQL_PROFILE_BECOME_LOCAL + " (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                    "become VARCHAR(255) NOT NULL, " +
                    "city VARCHAR(255), " +
                    "uploaded INT NOT NULL DEFAULT 0)";



    String SQL_CREATE_PROFILE_UPDATE =
            "CREATE TABLE " + SQL_PROFILE_UPDATE + " (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
                    "field_name VARCHAR(255) NOT NULL," +
                    "value VARCHAR(1024) NOT NULL," +
                    "uploaded INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_NO_TIP =
            "CREATE TABLE " + SQL_NO_TIP + " (" +
                    "chat_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "reasons VARCHAR(255) NOT NULL," +
                    "uploaded INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_TIP =
            "CREATE TABLE " + SQL_TIP + " (" +
                    "chat_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "tip INT NOT NULL," +
                    "rating DOUBLE NOT NULL," +
                    "review VARCHAR(1000)," +
                    "review_from_local VARCHAR(1000)," +
                    "paid INT NOT NULL DEFAULT 0," +
                    "uploaded INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_QUESTIONS_LOCALS =
            "CREATE TABLE " + SQL_QUESTIONS_LOCALS + " (" +
                    "question_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "question_value VARCHAR(1000) NOT NULL," +
                    "user_id VARCHAR(255) NOT NULL," +
                    "city_id VARCHAR(255) NOT NULL," +
                    "city_name VARCHAR(255) NOT NULL," +
                    "country_name VARCHAR(255) NOT NULL," +
                    "question_date VARCHAR(255) NOT NULL," +
                    "answered INT NOT NULL DEFAULT 0)";


    String SQL_CREATE_USERS =
            "CREATE TABLE " + SQL_USERS + " (" +
                    "user_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "user_name VARCHAR(255) NOT NULL DEFAULT ''," +
                    "photo VARCHAR(255) NOT NULL DEFAULT ''," +
                    "age VARCHAR(255)," +
                    "user_from_city_name VARCHAR(255) NOT NULL DEFAULT ''," +
                    "user_from_city_id VARCHAR(255) NOT NULL DEFAULT ''," +
                    "user_from_country_name VARCHAR(255) NOT NULL DEFAULT ''," +
                    "sentence VARCHAR(255) NOT NULL DEFAULT ''," +
                    "average_tip_received DOUBLE NOT NULL DEFAULT 0," +
                    "average_tip_given DOUBLE NOT NULL DEFAULT 0," +
                    "requests_visitor INT NOT NULL DEFAULT 0," +
                    "reviews INT NOT NULL DEFAULT 0," +
                    "isLocal INT NOT NULL DEFAULT 0," +
                    "requests_local INT NOT NULL DEFAULT 0," +
                    "rating INT NOT NULL DEFAULT 0," +
                    "timestamp DATETIME NULL," +
                    "expertise VARCHAR(255))";

    String SQL_CREATE_CHATS =
            "CREATE TABLE " + SQL_CHATS + " (" +
                    "chat_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "question_id VARCHAR(255)," +
                    "question_value VARCHAR(255)," +
                    "city_name VARCHAR(255)," +
                    "city_id VARCHAR(255)," +
                    "country_name VARCHAR(255)," +
                    "local_id VARCHAR(255)," +
                    "local_name VARCHAR(255)," +
                    "visitor_id VARCHAR(255)," +
                    "visitor_name VARCHAR(255)," +
                    "closed INT NOT NULL DEFAULT 0," +
                    "closedOnServer INT NOT NULL DEFAULT 0," +
                    "tipped INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_CHATS_MESSAGES =
            "CREATE TABLE " + SQL_CHATS_MESSAGES + " (" +
                    "message_id INT UNIQUE," +
                    "chat_id VARCHAR(255)," +
                    "type VARCHAR(20) DEFAULT 'message'," +
                    "content VARCHAR(1000)," +
                    "mine INT," +
                    "message_date DATETIME," +
                    "seen INT NOT NULL DEFAULT 0," +
                    "uploaded INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_QUESTIONS =
            "CREATE TABLE " + SQL_QUESTIONS + " (" +
                    "question_id VARCHAR(255) UNIQUE," +
                    "city_id VARCHAR(255) NOT NULL," +
                    "city_name VARCHAR(255) NOT NULL," +
                    "country_name VARCHAR(255)," +
                    "question_value VARCHAR(1000) NOT NULL," +
                    "question_date VARCHAR(50)," +
                    "sent INT NOT NULL DEFAULT 1)";

    String SQL_CREATE_QUESTIONS_ACCEPTED =
            "CREATE TABLE " + SQL_QUESTIONS_ACCEPTED + " (" +
                    "question_id VARCHAR(255) NOT NULL," +
                    "local_id VARCHAR(255) NOT NULL," +
                    "local_answer_id VARCHAR(255) NOT NULL," +
                    "answered INT NOT NULL DEFAULT 0)";

    String SQL_CREATE_CITIES =
            "CREATE TABLE " + SQL_CITIES + " (" +
                    "city_id VARCHAR(255) NOT NULL PRIMARY KEY, " +
                    "city_name VARCHAR(255), " +
                    "country_name VARCHAR(255))";

/*    String SQL_CREATE_EXPERTISE =
            "CREATE TABLE " + SQL_EXPERTISE + " (" +
                    "expertise_id VARCHAR(255) NOT NULL PRIMARY KEY, " +
                    "expertise_name VARCHAR(255))";*/

    String SQL_DELETE_CHAT =
            "DROP TABLE IF EXISTS " + SQL_CHATS;
    String SQL_DELETE_CHAT_ENTRIES =
            "DROP TABLE IF EXISTS " + SQL_CHATS_MESSAGES;
    String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + SQL_USERS;
    String SQL_DELETE_QUESTIONS =
            "DROP TABLE IF EXISTS " + SQL_QUESTIONS;
    String SQL_DELETE_QUESTIONS_LOCALS =
            "DROP TABLE IF EXISTS " + SQL_QUESTIONS_LOCALS;

    String SQL_DELETE_QUESTIONS_ACCEPTED =
            "DROP TABLE IF EXISTS " + SQL_QUESTIONS_ACCEPTED;
    String SQL_DELETE_CITIES =
            "DROP TABLE IF EXISTS " + SQL_CITIES;

    String SQL_DELETE_TIP =
            "DROP TABLE IF EXISTS " + SQL_TIP;

    String SQL_DELETE_NO_TIP =
            "DROP TABLE IF EXISTS " + SQL_NO_TIP;

    String SQL_DELETE_PROFILE_UPDATE =
            "DROP TABLE IF EXISTS " + SQL_PROFILE_UPDATE;

    String SQL_DELETE_REVIEW_LOCAL =
            "DROP TABLE IF EXISTS " + SQL_REVIEW_LOCAL;

    String SQL_DELETE_REVIEW_VISITOR =
            "DROP TABLE IF EXISTS " + SQL_REVIEW_VISITOR;

    String SQL_DELETE_USER_LOCAL_IN =
            "DROP TABLE IF EXISTS " + SQL_USER_LOCAL_IN;

    String SQL_DELETE_FAQS_LOCAL =
            "DROP TABLE IF EXISTS " + SQL_FAQS_LOCAL;

    String SQL_DELETE_FAQS_VISITOR =
            "DROP TABLE IF EXISTS " + SQL_FAQS_VISITOR;
    String SQL_DELETE_PROFILE_BECOME_LOCAL =
            "DROP TABLE IF EXISTS " + SQL_PROFILE_BECOME_LOCAL;


}
