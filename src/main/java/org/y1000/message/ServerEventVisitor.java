package org.y1000.message;

public interface ServerEventVisitor {

    default void visit(EntityEvent event) {

    }

    default void visit(AbstractPositionEvent positionEvent) {

    }

    default void visit(TurnEvent turnEvent){
        visit((AbstractPositionEvent)turnEvent);
    }

    default void visit(MoveEvent moveEvent) {
        visit((AbstractPositionEvent)moveEvent);
    }

    default void visit(SetPositionEvent setPositionEvent) {
        visit((AbstractPositionEvent)setPositionEvent);
    }

    default void visit(LoginMessage loginMessage) {

    }

    default void visit(InputResponseMessage inputResponseMessage) {

    }

}
