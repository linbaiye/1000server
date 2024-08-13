package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.AbstractPositionEvent;
import org.y1000.message.SetPositionEvent;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.util.Coordinate;

public final class Rope implements EntityEventListener  {

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
    private final Player moving;
    private int mills;
    private Coordinate from;

    public Rope(Player dragged, Player moving) {
        Validate.notNull(dragged);
        Validate.notNull(moving);
        Validate.isTrue(dragged.coordinate().directDistance(moving.coordinate()) <= 2);
        this.dragged = dragged;
        this.moving = moving;
        this.mills = 5000;
        from = Coordinate.Empty;
    }

    private int distance() {
        return dragged.coordinate().directDistance(moving.coordinate());
    }

    public void update(long delta) {
        mills -= (int)delta;
        follow();
    }

    private void follow() {
        var dist = distance();
        if (done() || dist < 1) {
            return;
        }
        var dir = dragged.coordinate().computeDirection(moving.coordinate());
        var nextPos = dragged.coordinate().moveBy(dir);
        if (nextPos.equals(moving.coordinate())) {
            if (dragged.direction() != dir) {
                dragged.changeDirection(dir);
                dragged.emitEvent(SetPositionEvent.of(dragged));
            }
            return;
        }
        var direction = AiPathUtil.computeNextMoveDirection(dragged, moving.coordinate(), from);
        if (direction == null) {
            return;
        }
        if (dragged.direction() != direction) {
            dragged.changeDirection(dir);
        } else {
            dragged.changeCoordinate(dragged.coordinate().moveBy(direction));
            from = dragged.coordinate();
        }
        dragged.emitEvent(SetPositionEvent.of(dragged));
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent != null && moving.equals(entityEvent.source()) &&
                entityEvent instanceof AbstractPositionEvent) {
            follow();
        } else if (entityEvent instanceof PlayerLeftEvent || moving.stateEnum() == State.DIE) {
            mills = 0;
        }
    }

    public boolean done() {
        return mills <= 0;
    }
}
