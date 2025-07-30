package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.event.PlayerFollowRopeEvent;
import org.y1000.entities.players.event.PlayerMovedEvent;
import org.y1000.util.Coordinate;

public final class Rope {

    /*
       TGotoXyRData = record ract, rdir, rlen : word; end;

// cdir, the dead one.
    function AI0GotoXy (var aret: TGotoXyRData; cdir, cx, cy, tx, ty, ox, oy: word; IsMoveable: TIsMoveable): Boolean;
var
   i : integer;
   key, len : word;
   lenarr : array [0..8-1] of word;
   mx, my: word;
begin
   Result := TRUE;
   aret.ract := AI_NONE;   aret.rlen := 0;
   if (cx = tx) and (cy = ty) then begin Result := FALSE; exit; end;    // ����

   key := GetNextDirection ( cx, cy, tx, ty);
   mx := cx; my := cy;
   GetNextPosition (key, mx, my);
   if (mx = tx) and (my = ty) then begin
      aret.rdir := key;
      aret.rlen := 1;
      if cdir <> key then begin aret.ract := AI_TURN; exit; end;
      if IsMoveable (mx, my) then begin aret.ract := AI_MOVE; exit; end;
      Result := FALSE;
      aret.ract := AI_DONTMOVE;
      exit;
   end;

   for i := 0 to 8-1 do lenarr[i] := 65535;

   if isMoveable (   cx, cy-1) then if (ox <> cx  ) or (oy <> cy-1) then lenarr[0] := (cx  -tx)*(cx  -tx) + (cy-1-ty)*(cy-1-ty);
   if isMoveable ( cx+1, cy-1) then if (ox <> cx+1) or (oy <> cy-1) then lenarr[1] := (cx+1-tx)*(cx+1-tx) + (cy-1-ty)*(cy-1-ty);
   if isMoveable ( cx+1, cy  ) then if (ox <> cx+1) or (oy <> cy  ) then lenarr[2] := (cx+1-tx)*(cx+1-tx) + (cy  -ty)*(cy  -ty);
   if isMoveable ( cx+1, cy+1) then if (ox <> cx+1) or (oy <> cy+1) then lenarr[3] := (cx+1-tx)*(cx+1-tx) + (cy+1-ty)*(cy+1-ty);
   if isMoveable (   cx, cy+1) then if (ox <> cx  ) or (oy <> cy+1) then lenarr[4] := (cx  -tx)*(cx  -tx) + (cy+1-ty)*(cy+1-ty);
   if isMoveable ( cx-1, cy+1) then if (ox <> cx-1) or (oy <> cy+1) then lenarr[5] := (cx-1-tx)*(cx-1-tx) + (cy+1-ty)*(cy+1-ty);
   if isMoveable ( cx-1, cy  ) then if (ox <> cx-1) or (oy <> cy  ) then lenarr[6] := (cx-1-tx)*(cx-1-tx) + (cy  -ty)*(cy  -ty);
   if isMoveable ( cx-1, cy-1) then if (ox <> cx-1) or (oy <> cy-1) then lenarr[7] := (cx-1-tx)*(cx-1-tx) + (cy-1-ty)*(cy-1-ty);

   len := 65535;
   for i := 0 to 8-1 do if len > lenarr[i] then begin key := i; len := lenarr[i]; end;
   mx := cx; my := cy;
   GetNextPosition (key, mx, my);
   aret.rdir := key;
   aret.rlen := len;
   if key <> cdir then begin aret.ract := AI_TURN; exit; end;
   if isMoveable ( mx, my) then aret.ract := AI_MOVE
   else aret.ract := AI_CLEAROLDPOS;
end;
     */

    private final Player dragged;
    private final Player dragging;
    private int mills;
    private Coordinate from;
    private int stepCounterMillis;

    private boolean moving;

    public Rope(Player dragging, Player dragged) {
        Validate.isTrue(!dragging.equals(dragged));
        this.dragging = dragging;
        this.dragged = dragged;
        this.mills = 5000;
        from = Coordinate.Empty;
        stepCounterMillis = 0;
        moving = false;
    }

    private int distance() {
        return dragged.coordinate().directDistance(dragging.coordinate());
    }

    public void update(int delta) {
        if (isBroken())
            return;
        follow(delta);
        if (mills >= delta)
            mills -= delta;
    }

    public boolean isBroken() {
        return !dragged.canBeDragged() ||
                dragging.isLeftGame() ||
                dragging.getRealm().id() != dragged.getRealm().id() ||
                mills <= 0;
    }

    public boolean isDragging(Player dragging, Player dragged) {
        return this.dragging.equals(dragging) && this.dragged.equals(dragged);
    }

    private boolean coordinateMovable(Coordinate coordinate) {
        return dragged.realmMap().tileMovable(coordinate) && !coordinate.equals(from);
    }

    private void follow(int delta) {
        stepCounterMillis -= delta;
        if (stepCounterMillis > 0)
            return;
        if (moving) {
            moving = false;
            from = dragged.coordinate();
            dragged.changeCoordinate(dragged.coordinate().moveBy(dragged.direction()));
            dragged.sendEvent(new PlayerMovedEvent(dragged));
        }
        stepCounterMillis = 200;
        var dir = dragged.coordinate().bestDirectionTo(dragging.coordinate(), this::coordinateMovable);
        var dist = distance();
        if (dist < 2) {
            if (dir != null && dragged.direction() != dir) {
                dragged.changeDirection(dir);
                dragged.sendEvent(PlayerFollowRopeEvent.turn(dragged));
            }
        } else {
            if (dir != null) {
                dragged.changeDirection(dir);
                dragged.sendEvent(PlayerFollowRopeEvent.follow(dragged));
                moving = true;
            }
        }
    }
}
