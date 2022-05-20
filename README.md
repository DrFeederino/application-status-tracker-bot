# iData Application Tracking Bot

This bot is designed to automatically track application status on the iData website.

# Roadmap

* Add bots abilities: /news, /list, /remove & /about;
* Validation of input & additional notifications to users if something is wrong;
* Automatic deletion of user info if documents were shipped;
* Documenting code;
* Refactoring & fixing bugs;

# Hows and whats:

* WebCheckerBot.java is the core logic for the bot, all messages and states are handled there.
* TrackingParser.java is an abstract class for the core logic of tracking provider. Any support for future tracking
  provider should be extended from it since the scheduler calls child's update function.
* Entities contain all necessary constants, db entities and enums to handle user state and info.
* Scheduling is set up to check hourly, could be tweaked to your liking.
* The checks are implemented with the support of headless Selenium Firefox driver.
* In order to work, requires env vars:
  * JDBC_URL - jdbc url for the db's table;
  * JDBC_USERNAME - username for the table;
  * JDBC_PASSWORD - password for the username of table;
  * BOT_TOKEN - telegram bot's token, which is given after creating one with BotFather;
  * BOT_NAME - bot's name for the telegram-bots API;
  * AES_SALT - salt for hashing personal data in the DB;
  * AES_PASSWORD - password used for hashing;
  * (Optional) webdriver.gecko.driver - used for specifying driver's path for use by the application.
