package org.y1000.sdb;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.ValueEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public final class AtdParser {

    private static final Map<String, OldPlayerStateEnum> STATE_MAP = new HashMap<>()
    {{
        put("MOVE", OldPlayerStateEnum.Move);
        put("TURNNING", OldPlayerStateEnum.IDLE);
        put("STRUCTED", OldPlayerStateEnum.HURT);
        put("HIT1", OldPlayerStateEnum.ATTACK);
        put("DIE", OldPlayerStateEnum.DIE);
        put("TURN", OldPlayerStateEnum.Turn);
    }};

    private static final Map<String, Direction> DIRECTION_MAP = new HashMap<>()
    {{
        put("DR_0", Direction.UP);
        put("DR_1", Direction.UP_RIGHT);
        put("DR_2", Direction.RIGHT);
        put("DR_3", Direction.DOWN_RIGHT);
        put("DR_4", Direction.DOWN);
        put("DR_5", Direction.DOWN_LEFT);
        put("DR_6", Direction.LEFT);
        put("DR_7", Direction.UP_LEFT);
    }};

    private static String DecodeToString(byte[] bytes)
    {
        for (int i = 0; i < bytes.length; i++)
        {
            var b = bytes[i];
            var l = 0x0f & b;
            var h = 0xf0 & b;
            bytes[i] = (byte)((h >> 4) + (l << 4));
        }
        int len = bytes[0] & 0xff;
        if (len == 0) {
            return null;
        }
        return new String(bytes, 1, len);
    }

    private static List<String> readStrings(InputStream inputStream) throws IOException {
        List<String> list = new ArrayList<>();
        int cnt = inputStream.available() / 255;
        for (int i = 0; i < cnt; i++)
        {
            var buffer = new byte[255];
            int n = inputStream.read(buffer);
            if (n <= 0)
            {
                break;
            }
            var convert = DecodeToString(buffer);
            if (convert != null)
            {
                list.add(convert);
            }
        }
        return list;
    }

    /*

     */
    private List<AnimationDescriptor> parseTo(List<String> lines) {

        List<AnimationDescriptor> result = new ArrayList<>();
        for (var str : lines)
        {
            log.debug(str);
            var tokens = str.replace("\\s+", "").split(",");
            if (tokens[0] == null || "Name".equals(tokens[0]))
            {
                continue;
            }
            String action = tokens[1];
            String direction = tokens[2];
            int frameNumber = Integer.parseInt(tokens[3]);
            int frameTime = Integer.parseInt(tokens[4]);
            int startFrame = Integer.parseInt(tokens[5]);
            result.add(new AnimationDescriptor(STATE_MAP.get(action), DIRECTION_MAP.get(direction), startFrame, frameNumber, frameTime));
        }
        return result;

    }


    public Map<String, List<AnimationDescriptor>> parse(Set<String> animateIds) {
        Map<String, List<AnimationDescriptor>> result = new HashMap<>();
        try {
            if (animateIds == null || animateIds.isEmpty())
                return Collections.emptyMap();
            for (String animateId : animateIds) {
                try (var input = getClass().getResourceAsStream("/atd/" + animateId + ".atd")) {
                    if (input == null) {
                        throw new RuntimeException("Not found atd " + animateId);
                    }
                    List<String> strings = readStrings(input);
                    result.put(animateId, parseTo(strings));
                }
            }
            return result;
        } catch (RuntimeException e) {
            log.error("Failed to read atd files.", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to read atd files.", e);
            throw new RuntimeException("Failed load atd.");
        }
    }

//    private static class AtdSdb extends AbstractCSVSdbReader {
//           try (var inputstream = getClass().getResourceAsStream("/sdb/" + name)) {
//            if (inputstream == null) {
//                throw new NoSuchElementException("Sdb does not exist, " + name);
//            }
//            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputstream, Charset.forName(charset)))) {
//                read(bufferedReader);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static record AnimationDescriptor(OldPlayerStateEnum playerStateEnum, Direction direction, int startFrame, int frameNumber, int tickPerFrame) {
        public int animationLength() {
            return tickPerFrame * 10;
        }
    }

    @Deprecated
    public enum OldPlayerStateEnum implements ValueEnum {
        IDLE(1),

        Move(2),

        RUN(3),

        STANDUP(4),

        HURT(6),

        DIE(7),

        ENFIGHT_WALK(8),

        BOW(9),

        SIT(10),

        FLY(11),

        ATTACK(12),

        FightStand(13),

        HELLO(14),

        FIST(15),

        KICK(16),

        SWORD(17),

        SWORD2H(18),

        BLADE(19),

        BLADE2H(20),

        AXE(21),

        SPEAR(22),

        THROW(23),

        Turn(24),

        ;

        private final int v;

        OldPlayerStateEnum(int v) {
            this.v = v;
        }

        @Override
        public int value() {
            return v;
        }

        public static OldPlayerStateEnum valueOf(int v) {
            return ValueEnum.getTypeOrThrow(OldPlayerStateEnum.values(), v);
        }
    }
}
