package de.marcermarc.sleep.controller;

public class ConfigController {

    private PluginController controller;

    private double percentOfPlayerMustSleep;
    private String messageAfterSleep, messageSomeoneGoSleep, messageSomeoneGetUp;

    public ConfigController(PluginController controller) {
        this.controller = controller;

        setDefaultConfig();

        load();
    }

    private void setDefaultConfig() {

        controller.getMain().getConfig().addDefault("percentOfPlayerMustSleep", 0.5);
        controller.getMain().getConfig().addDefault("messageAfterSleep", "Good Morning!");
        controller.getMain().getConfig().addDefault("messageSomeoneGoSleep", "@player sleeps now! (@sleepingPlayer/@mustSleepPlayer)");
        controller.getMain().getConfig().addDefault("messageSomeoneGetUp", "@player get up! (@sleepingPlayer/@mustSleepPlayer)");

        controller.getMain().getConfig().options().copyDefaults(true);
        controller.getMain().saveDefaultConfig();
    }

    private void load() {
        percentOfPlayerMustSleep = controller.getMain().getConfig().getDouble("percentOfPlayerMustSleep");
        messageAfterSleep = controller.getMain().getConfig().getString("messageAfterSleep");
        messageSomeoneGoSleep = controller.getMain().getConfig().getString("messageSomeoneGoSleep");
        messageSomeoneGetUp = controller.getMain().getConfig().getString("messageSomeoneGetUp");
    }

    //region getters and setters

    public double getPercentOfPlayerMustSleep() {
        return percentOfPlayerMustSleep;
    }

    public void setPercentOfPlayerMustSleep(double percentOfPlayerMustSleep) {
        this.percentOfPlayerMustSleep = percentOfPlayerMustSleep;
    }

    public String getMessageAfterSleep() {
        return messageAfterSleep;
    }

    public void setMessageAfterSleep(String messageAfterSleep) {
        this.messageAfterSleep = messageAfterSleep;
    }

    public String getMessageSomeoneGoSleep() {
        return messageSomeoneGoSleep;
    }

    public void setMessageSomeoneGoSleep(String messageSomeoneGoSleep) {
        this.messageSomeoneGoSleep = messageSomeoneGoSleep;
    }

    public String getMessageSomeoneGetUp() {
        return messageSomeoneGetUp;
    }

    public void setMessageSomeoneGetUp(String messageSomeoneGetUp) {
        this.messageSomeoneGetUp = messageSomeoneGetUp;
    }

    //endregion
}
