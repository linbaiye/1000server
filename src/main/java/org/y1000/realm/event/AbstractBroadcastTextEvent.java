package org.y1000.realm.event;

import org.y1000.message.serverevent.TextMessage;

public abstract class AbstractBroadcastTextEvent implements BroadcastEvent {
    protected final String text;
    protected final TextMessage.TextType textType;
    protected final TextMessage.ColorType colorType;
    protected final TextMessage.Location location;

    public AbstractBroadcastTextEvent(String text, TextMessage.TextType textType, TextMessage.ColorType colorType, TextMessage.Location location) {
        this.text = text;
        this.textType = textType;
        this.colorType = colorType;
        this.location = location;
    }
}
