package org.y1000.exp;


public final class ExperienceUtil {

    public static final int DEFAULT_EXP = 10000;
    private final int level;
    private final int exp;
    private final int gap;
    private final int maxExp;

    public static final int MAX_EXP = 1085138172;

    public ExperienceUtil(int level, int exp, int gap, int maxExp) {
        this.level = level;
        this.exp = exp;
        this.gap = gap;
        this.maxExp = maxExp;
    }
/*
type
   TLevelData = record
     rlevel : integer;
     rexp : integer;
     rgap : integer;
     rGetMaxExp : integer;
   end;
   PTLevelData = ^TLevelData;
     */

    private static final int[] EXPS = new int[] {
            1, -1, 107, 21,
            2, 107, 140, 23,
            3, 247, 182, 26,
            4, 428, 235, 29,
            5, 664, 304, 33,
            6, 968, 391, 39,
            7, 1358, 501, 45,
            8, 1859, 638, 53,
            9, 2497, 811, 62,
            10, 3309, 1027, 68,
            11, 4336, 1295, 86,
            12, 5631, 1628, 101,
            13, 7259, 2040, 127,
            14, 9299, 2546, 149,
            15, 11845, 3169, 186,
            16, 15014, 3931, 218,
            17, 18944, 4860, 270,
            18, 23805, 5992, 315,
            19, 29796, 7365, 387,
            20, 37161, 9025, 451,
            21, 46187, 11029, 525,
            22, 57215, 13439, 584,
            23, 70654, 16329, 680,
            24, 86983, 19787, 761,
            25, 106771, 23912, 885,
            26, 130683, 28821, 993,
            27, 159504, 34645, 1154,
            28, 194148, 41537, 1298,
            29, 235686, 49674, 1505,
            30, 285359, 59253, 1692,
            31, 344612, 70503, 1905,
            32, 415115, 83680, 2145,
            33, 498796, 99078, 2416,
            34, 597873, 117024, 2721,
            35, 714897, 137888, 3064,
            36, 852785, 162086, 3448,
            37, 1014871, 190082, 3879,
            38, 1204953, 222393, 4360,
            39, 1427346, 259594, 4898,
            40, 1686940, 302322, 5496,
            41, 1989262, 351282, 6056,
            42, 2340544, 407249, 6568,
            43, 2747793, 471075, 7247,
            44, 3218867, 543691, 7879,
            45, 3762558, 626114, 8696,
            46, 4388672, 719450, 9466, 47, 5108122, 824899, 10441, 48, 5933021, 943753, 11370, 49, 6876774, 1077408, 12528, 50, 7954182, 1227359, 13637,
            51, 9181541, 1395203, 14686, 52, 10576744, 1582642, 15669, 53, 12159386, 1791483, 16900, 54, 13950868, 2023631, 18068, 55, 15974500, 2281096, 19496,
            56, 18255595, 2565979, 20861, 57, 20821574, 2880474, 22503, 58, 23702048, 3226859, 24081, 59, 26928907, 3607485, 25953, 60, 30536393, 4024770, 27757,
            61, 34561163, 4481182, 29098, 62, 39042345, 4979228, 30547, 63, 44021573, 5521438, 32101, 64, 49543011, 6110342, 33758, 65, 55653353, 6748455, 35518,
            66, 62401807, 7438251, 37378, 67, 69840059, 8182141, 39337, 68, 78022199, 8982441, 41393, 69, 87004640, 9841350, 43545, 70, 96845990, 10760913, 45791,
            71, 107606903, 11742996, 47160, 72, 119349899, 12789244, 48444, 73, 132139142, 13901053, 50003, 74, 146040195, 15079532, 51465, 75, 161119727, 16325465, 53177,
            76, 177445192, 17639277, 54780, 77, 195084469, 19020997, 56610, 78, 214105466, 20470218, 58319, 79, 234575684, 21986069, 60235, 80, 256561752, 23567175, 62018,
            81, 280128928, 25211630, 63505, 82, 305340558, 26916966, 65016, 83, 332257524, 28680123, 66543, 84, 360937647, 30497435, 68074, 85, 391435082, 32364602, 69601,
            86, 423799685, 34276681, 71113, 87, 458076366, 36228076, 72601, 88, 494304442, 38212533, 74055, 89, 532516975, 40223140, 75465, 90, 572740115, 42252340, 76822,
            91, 614992455, 44291942, 79805, 92, 659284397, 46333143, 82590, 93, 705617540, 48366557, 85453, 94, 753984097, 50382250, 88080, 95, 804366346, 52369783, 90762,
            96, 856736129, 54318261, 93170, 97, 911054390, 56216393, 95606,
            98, 967270783, 58052549, 97731,
            99, 1025323333, 59814839, 99691,
            100, MAX_EXP, 60000000, 0
    };

    private static final ExperienceUtil[] EXPERIENCES = buildArray();

    private static ExperienceUtil[] buildArray() {
        ExperienceUtil[] experiences = new ExperienceUtil[100];
        for (int i = 0; i < 100; i++) {
            int offset = i * 4;
            experiences[i] = new ExperienceUtil(EXPS[offset],  EXPS[offset + 1], EXPS[offset + 2], EXPS[offset + 3]);
        }
        return experiences;
    }


    /*
function GetLevel (aexp: integer): integer;
var
   i, rm, tempgap : integer;
   p, pold : PTLevelData;
begin
   if aexp <= 0 then begin Result := 100; exit; end;
   if aexp >= 1085138172 then begin Result := 9999; exit; end;

   p := PTLevelData(@LevelsArr);
   pold := p;  inc (p);

   Result := 100;
   for i := 2 to 100 do begin
      if (aexp < p^.rexp) and (aexp >= pold^.rexp) then begin
         rm := aexp - pold^.rexp;
         if pold^.rexp < 10000000 then begin
            Result := (i-1)*100 + rm*100 div pold^.rgap;
         end else begin
            tempgap := pold^.rgap div 100;
            rm := rm div 100;
            Result := (i-1)*100 + rm*100 div tempgap;
         end;
         exit;
      end;
      pold := p;
      inc (p);
   end;
end;
     */

    public static int computeLevel(int exp) {
        if (exp <=  0) {
            return 100;
        } else if (exp >= 1085138172) {
            return 9999;
        }
        int rm, tempgap;
        int index = 1, previous = 0;
        int result = 100;
        for (int i = 2; i <= 100; i++, index++, previous++) {
            if (exp < EXPERIENCES[index].exp && exp >= EXPERIENCES[previous].exp) {
                rm = exp - EXPERIENCES[previous].exp;
                if (EXPERIENCES[previous].exp < 10000000) {
                    result = (i - 1) * 100 + rm * 100 / EXPERIENCES[previous].gap;
                } else {
                    tempgap = EXPERIENCES[previous].gap / 100;
                    rm /= 100;
                    result = (i - 1) * 100  + rm * 100 / tempgap;
                }
                break;
            }
        }
        return result;
    }

    public static int GetLevelMaxExp(int level) {
       /*

function GetLevelMaxExp (alevel: integer): integer;
var
   n : integer;
   p : PTLevelData;
begin
   Result := 0;
   if alevel < 100 then exit;
   if alevel >= 9999 then exit;
   n := alevel div 100;
   p := PTLevelData(@LevelsArr);
   inc (p,n-1);
   Result := p^.rGetMaxExp;
end;
        */
        int result = 0;
        if (level < 100 | level >= 9999) {
            return result;
        }
        return EXPERIENCES[(level / 100) - 1].maxExp;
    }


    private static int addExp(int level, int currentExp, int addingExp) {
        int n = GetLevelMaxExp(level) * 3;
        if (n > addingExp) {
            n = addingExp;
        }
        //currentlevel = ComputeLevel(currentExp + n);
        return n;
        /*
        var n : integer;
begin
   n := GetLevelMaxExp (aLevel) * 3;
   if n > addvalue then n := addvalue;
   inc (aExp, n);
   aLevel := GetLevel (aExp);
   Result := n;
         */
    }

    /*
    oldslevel := pCurAttackMagic.rcSkillLevel;
    Result := AddPermitExp (pCurAttackMagic.rcSkillLevel, pCurAttackMagic.rSkillExp, aexp);
   if oldslevel <> pCurAttackMagic.rcSkillLevel then begin
    FindAndSendMagic (pCurAttackMagic);
      FSendClass.SendEventString (StrPas (@pCurAttackMagic^.rname));
      MagicClass.Calculate_cLifeData (pCurAttackMagic);
    end;*/

    //private static int currentlevel = 100;

    private static int currentExp = 0;

    public static void main(String[] args) {
        //int i = ComputeLevel(10000);
        for (int i = 0; i < 10000; i++) {
            var level = computeLevel(currentExp);
            int n = GetLevelMaxExp(level) * 3;
            if (n > 10000) {
                n = 10000;
            }
            currentExp += n;
            System.out.println("Level " + level + ", exp " + currentExp);
        }
    }
}
