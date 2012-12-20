package com.pubnub.test;

import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.Callback;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

public class PubnubTestMIDlet extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    private Command exitCommand;
    private Command publishCommand;
    private Command timeCommand;
    private Command historyCommand;
    private Command unsubscribeCommand;
    private Command subscribeCommand;
    private Command hereNowCommand;
    private Command presenceCommand;
    private Command detailedHistoryCommand;
    private Form form;
    private StringItem stringItem;
    String Channel = "hello_world";

    public PubnubTestMIDlet() {
    }


    Pubnub _pubnub = new Pubnub("demo", "demo", "demo", "", false);

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {
        switchDisplayable(null, getForm());
        subscribe();
    }

    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {
    }

    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {
        Display display = getDisplay();
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }
    }

    /**
     * Called by a system to indicated that a command has been invoked on a
     * particular displayable.
     *
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {
        if (displayable == form) {
            if (command == exitCommand) {
                exitMIDlet();
            } else if (command == publishCommand) {
                publish();
            } else if (command == subscribeCommand) {
                subscribe();
            }
        }
    }

    /**
     * Returns an initiliazed instance of exitCommand component.
     *
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {
            exitCommand = new Command("Exit", Command.EXIT, 0);
        }
        return exitCommand;
    }

    /**
     * Returns an initiliazed instance of form component.
     *
     * @return the initialized component instance
     */
    public Form getForm() {
        if (form == null) {
            form = new Form("Welcome", new Item[]{getStringItem()});
            form.addCommand(getExitCommand());
            form.addCommand(getPublishCommand());
            form.addCommand(getSubscribeCommand());
            form.setCommandListener(this);
        }
        return form;
    }

    /**
     * Returns an initiliazed instance of stringItem component.
     *
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {
            stringItem = new StringItem("Pubnub", "Hello Pubnub!", Item.PLAIN);
        }
        return stringItem;
    }


    /**
     * Returns an initiliazed instance of publishCommand component.
     *
     * @return the initialized component instance
     */
    public Command getPublishCommand() {
        if (publishCommand == null) {
            publishCommand = new Command("Publish", Command.ITEM, 0);
        }
        return publishCommand;
    }

   

    /**
     * Returns an initiliazed instance of subscribeCommand component.
     *
     * @return the initialized component instance
     */
    public Command getSubscribeCommand() {
        if (subscribeCommand == null) {
            subscribeCommand = new Command("Subscribe", Command.ITEM, 0);
        }
        return subscribeCommand;
    }



    /**
     * Returns a display instance.
     *
     * @return the display instance.
     */
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started. Checks whether the MIDlet have been
     * already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {

            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     *
     * @param unconditional if true, then the MIDlet has to be unconditionally
     * terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }

    public void publish() {
        try {
            JSONObject message = new JSONObject();
            message.put("some_key", "j2me says hello, world!");

            Hashtable args = new Hashtable(2);
            args.put("channel", Channel); // Channel Name
            args.put("message", message); // JSON Message
            _pubnub.publish(args, new Callback() {
                public void successCallback(String channel, Object message) {
                    
                }
             });

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribe() {
        Hashtable args = new Hashtable(6);
        args.put("channels", new String[]{Channel});

        try {
        _pubnub.subscribe(args, new Callback() {
            public void successCallback(String channel, Object message) {
                System.out.println("Message recevie on channel:" + channel
                        + " Message:" + message.toString());
                try {
                    if (message instanceof JSONObject) {
                        JSONObject obj = (JSONObject) message;
                        Alert a = new Alert("Received", obj.toString(), null, null);
                        a.setTimeout(Alert.FOREVER);
                        getDisplay().setCurrent(a, form);

                        Enumeration keys = obj.keys();
                        while (keys.hasMoreElements()) {
                            System.out.println(obj.get(keys.nextElement().toString())
                                    + " ");
                        }

                    } else if (message instanceof String) {
                        String obj = (String) message;
                        System.out.print(obj + " ");
                        System.out.println();

                        Alert a = new Alert("Received", obj.toString(), null, null);
                        a.setTimeout(Alert.FOREVER);
                        getDisplay().setCurrent(a, form);
                    } else if (message instanceof JSONArray) {
                        JSONArray obj = (JSONArray) message;
                        System.out.print(obj.toString() + " ");
                        System.out.println();

                        Alert a = new Alert("Received", obj.toString(), null, null);
                        a.setTimeout(Alert.FOREVER);
                        getDisplay().setCurrent(a, form);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    } catch (Exception e){
        e.printStackTrace();
    }
    }

 

}