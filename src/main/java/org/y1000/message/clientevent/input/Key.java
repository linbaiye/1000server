package org.y1000.message.clientevent.input;

import org.y1000.message.ValueEnum;

// Copied from godot.
public enum Key implements ValueEnum {
    /// <summary>
    /// <para>Enum value which doesn't correspond to any key. This is used to initialize <see cref="T:Godot.Key" /> properties with a generic state.</para>
    /// </summary>
    None(0),
    /// <summary>
    /// <para>Space key.</para>
    /// </summary>

    Space(32), // 0x0000000000000020
    /// <summary>
    /// <para>! key.</para>
    /// </summary>
    Exclam(33), // 0x0000000000000021
    /// <summary>
    /// <para>" key.</para>
    /// </summary>
    Quotedbl(34), // 0x0000000000000022
    /// <summary>
    /// <para># key.</para>
    /// </summary>
    Numbersign(35), // 0x0000000000000023
    /// <summary>
    /// <para>$ key.</para>
    /// </summary>
    Dollar(36), // 0x0000000000000024
    /// <summary>
    /// <para>% key.</para>
    /// </summary>
    Percent(37), // 0x0000000000000025
    /// <summary>
    /// <para>&amp; key.</para>
    /// </summary>
    Ampersand(38), // 0x0000000000000026
    /// <summary>
    /// <para>' key.</para>
    /// </summary>
    Apostrophe(39), // 0x0000000000000027
    /// <summary>
    /// <para>( key.</para>
    /// </summary>
    Parenleft(40), // 0x0000000000000028
    /// <summary>
    /// <para>) key.</para>
    /// </summary>
    Parenright(41), // 0x0000000000000029
    /// <summary>
    /// <para>* key.</para>
    /// </summary>
    Asterisk(42), // 0x000000000000002A
    /// <summary>
    /// <para>+ key.</para>
    /// </summary>
    Plus(43), // 0x000000000000002B
    /// <summary>
    /// <para>, key.</para>
    /// </summary>
    Comma(44), // 0x000000000000002C
    /// <summary>
    /// <para>- key.</para>
    /// </summary>
    Minus(45), // 0x000000000000002D
    /// <summary>
    /// <para>. key.</para>
    /// </summary>
    Period(46), // 0x000000000000002E
    /// <summary>
    /// <para>/ key.</para>
    /// </summary>
    Slash(47), // 0x000000000000002F
    /// <summary>
    /// <para>Number 0 key.</para>
    /// </summary>
    Key0(48), // 0x0000000000000030
    /// <summary>
    /// <para>Number 1 key.</para>
    /// </summary>
    Key1(49), // 0x0000000000000031
    /// <summary>
    /// <para>Number 2 key.</para>
    /// </summary>
    Key2(50), // 0x0000000000000032
    /// <summary>
    /// <para>Number 3 key.</para>
    /// </summary>
    Key3(51), // 0x0000000000000033
    /// <summary>
    /// <para>Number 4 key.</para>
    /// </summary>
    Key4(52), // 0x0000000000000034
    /// <summary>
    /// <para>Number 5 key.</para>
    /// </summary>
    Key5(53), // 0x0000000000000035
    /// <summary>
    /// <para>Number 6 key.</para>
    /// </summary>
    Key6(54), // 0x0000000000000036
    /// <summary>
    /// <para>Number 7 key.</para>
    /// </summary>
    Key7(55), // 0x0000000000000037
    /// <summary>
    /// <para>Number 8 key.</para>
    /// </summary>
    Key8(56), // 0x0000000000000038
    /// <summary>
    /// <para>Number 9 key.</para>
    /// </summary>
    Key9(57), // 0x0000000000000039
    /// <summary>
    /// <para>: key.</para>
    /// </summary>
    Colon(58), // 0x000000000000003A
    /// <summary>
    /// <para>; key.</para>
    /// </summary>
    Semicolon(59), // 0x000000000000003B
    /// <summary>
    /// <para>&lt; key.</para>
    /// </summary>
    Less(60), // 0x000000000000003C
    /// <summary>
    /// <para>= key.</para>
    /// </summary>
    Equal(61), // 0x000000000000003D
    /// <summary>
    /// <para>&gt; key.</para>
    /// </summary>
    Greater(62), // 0x000000000000003E
    /// <summary>
    /// <para>? key.</para>
    /// </summary>
    Question(63), // 0x000000000000003F
    /// <summary>
    /// <para>@ key.</para>
    /// </summary>
    At(64), // 0x0000000000000040
    /// <summary>
    /// <para>A key.</para>
    /// </summary>
    A(65), // 0x0000000000000041
    /// <summary>
    /// <para>B key.</para>
    /// </summary>
    B(66), // 0x0000000000000042
    /// <summary>
    /// <para>C key.</para>
    /// </summary>
    C(67), // 0x0000000000000043
    /// <summary>
    /// <para>D key.</para>
    /// </summary>
    D(68), // 0x0000000000000044
    /// <summary>
    /// <para>E key.</para>
    /// </summary>
    E(69), // 0x0000000000000045
    /// <summary>
    /// <para>F key.</para>
    /// </summary>
    F(70), // 0x0000000000000046
    /// <summary>
    /// <para>G key.</para>
    /// </summary>
    G(71), // 0x0000000000000047
    /// <summary>
    /// <para>H key.</para>
    /// </summary>
    H(72), // 0x0000000000000048
    /// <summary>
    /// <para>I key.</para>
    /// </summary>
    I(73), // 0x0000000000000049
    /// <summary>
    /// <para>J key.</para>
    /// </summary>
    J(74), // 0x000000000000004A
    /// <summary>
    /// <para>K key.</para>
    /// </summary>
    K(75), // 0x000000000000004B
    /// <summary>
    /// <para>L key.</para>
    /// </summary>
    L(76), // 0x000000000000004C
    /// <summary>
    /// <para>M key.</para>
    /// </summary>
    M(77), // 0x000000000000004D
    /// <summary>
    /// <para>N key.</para>
    /// </summary>
    N(78), // 0x000000000000004E
    /// <summary>
    /// <para>O key.</para>
    /// </summary>
    O(79), // 0x000000000000004F
    /// <summary>
    /// <para>P key.</para>
    /// </summary>
    P(80), // 0x0000000000000050
    /// <summary>
    /// <para>Q key.</para>
    /// </summary>
    Q(81), // 0x0000000000000051
    /// <summary>
    /// <para>R key.</para>
    /// </summary>
    R(82), // 0x0000000000000052
    /// <summary>
    /// <para>S key.</para>
    /// </summary>
    S(83), // 0x0000000000000053
    /// <summary>
    /// <para>T key.</para>
    /// </summary>
    T(84), // 0x0000000000000054
    /// <summary>
    /// <para>U key.</para>
    /// </summary>
    U(85), // 0x0000000000000055
    /// <summary>
    /// <para>V key.</para>
    /// </summary>
    V(86), // 0x0000000000000056
    /// <summary>
    /// <para>W key.</para>
    /// </summary>
    W(87), // 0x0000000000000057
    /// <summary>
    /// <para>X key.</para>
    /// </summary>
    X(88), // 0x0000000000000058
    /// <summary>
    /// <para>Y key.</para>
    /// </summary>
    Y(89), // 0x0000000000000059
    /// <summary>
    /// <para>Z key.</para>
    /// </summary>
    Z(90), // 0x000000000000005A
    /// <summary>
    /// <para>[ key.</para>
    /// </summary>
    Bracketleft(91), // 0x000000000000005B
    /// <summary>
    /// <para>\ key.</para>
    /// </summary>
    Backslash(92), // 0x000000000000005C
    /// <summary>
    /// <para>] key.</para>
    /// </summary>
    Bracketright(93), // 0x000000000000005D
    /// <summary>
    /// <para>^ key.</para>
    /// </summary>
    Asciicircum(94), // 0x000000000000005E
    /// <summary>
    /// <para>_ key.</para>
    /// </summary>
    Underscore(95), // 0x000000000000005F
    /// <summary>
    /// <para>` key.</para>
    /// </summary>
    Quoteleft(96), // 0x0000000000000060
    /// <summary>
    /// <para>{ key.</para>
    /// </summary>
    Braceleft(123), // 0x000000000000007B
    /// <summary>
    /// <para>| key.</para>
    /// </summary>
    Bar(124), // 0x000000000000007C
    /// <summary>
    /// <para>} key.</para>
    /// </summary>
    Braceright(125), // 0x000000000000007D
    /// <summary>
    /// <para>~ key.</para>
    /// </summary>
    Asciitilde(126), // 0x000000000000007E
    /// <summary>
    /// <para>¥ key.</para>
    /// </summary>
    Yen(165), // 0x00000000000000A5
    /// <summary>
    /// <para>§ key.</para>
    /// </summary>
    Section(167), // 0x00000000000000A7
    /// <summary>
    /// <para>Keycodes with this bit applied are non-printable.</para>
    /// </summary>
    Special(4194304), // 0x0000000000400000
    /// <summary>
    /// <para>Escape key.</para>
    /// </summary>
    Escape(4194305), // 0x0000000000400001
    /// <summary>
    /// <para>Tab key.</para>
    /// </summary>
    Tab(4194306), // 0x0000000000400002
    /// <summary>
    /// <para>Shift + Tab key.</para>
    /// </summary>
    Backtab(4194307), // 0x0000000000400003
    /// <summary>
    /// <para>Backspace key.</para>
    /// </summary>
    Backspace(4194308), // 0x0000000000400004
    /// <summary>
    /// <para>Return key (on the main keyboard).</para>
    /// </summary>
    Enter(4194309), // 0x0000000000400005
    /// <summary>
    /// <para>Enter key on the numeric keypad.</para>
    /// </summary>
    KpEnter(4194310), // 0x0000000000400006
    /// <summary>
    /// <para>Insert key.</para>
    /// </summary>
    Insert(4194311), // 0x0000000000400007
    /// <summary>
    /// <para>Delete key.</para>
    /// </summary>
    Delete(4194312), // 0x0000000000400008
    /// <summary>
    /// <para>Pause key.</para>
    /// </summary>
    Pause(4194313), // 0x0000000000400009
    /// <summary>
    /// <para>Print Screen key.</para>
    /// </summary>
    Print(4194314), // 0x000000000040000A
    /// <summary>
    /// <para>System Request key.</para>
    /// </summary>
    Sysreq(4194315), // 0x000000000040000B
    /// <summary>
    /// <para>Clear key.</para>
    /// </summary>
    Clear(4194316), // 0x000000000040000C
    /// <summary>
    /// <para>Home key.</para>
    /// </summary>
    Home(4194317), // 0x000000000040000D
    /// <summary>
    /// <para>End key.</para>
    /// </summary>
    End(4194318), // 0x000000000040000E
    /// <summary>
    /// <para>Left arrow key.</para>
    /// </summary>
    Left(4194319), // 0x000000000040000F
    /// <summary>
    /// <para>Up arrow key.</para>
    /// </summary>
    Up(4194320), // 0x0000000000400010
    /// <summary>
    /// <para>Right arrow key.</para>
    /// </summary>
    Right(4194321), // 0x0000000000400011
    /// <summary>
    /// <para>Down arrow key.</para>
    /// </summary>
    Down(4194322), // 0x0000000000400012
    /// <summary>
    /// <para>Page Up key.</para>
    /// </summary>
    Pageup(4194323), // 0x0000000000400013
    /// <summary>
    /// <para>Page Down key.</para>
    /// </summary>
    Pagedown(4194324), // 0x0000000000400014
    /// <summary>
    /// <para>Shift key.</para>
    /// </summary>
    Shift(4194325), // 0x0000000000400015
    /// <summary>
    /// <para>Control key.</para>
    /// </summary>
    Ctrl(4194326), // 0x0000000000400016
    /// <summary>
    /// <para>Meta key.</para>
    /// </summary>
    Meta(4194327), // 0x0000000000400017
    /// <summary>
    /// <para>Alt key.</para>
    /// </summary>
    Alt(4194328), // 0x0000000000400018
    /// <summary>
    /// <para>Caps Lock key.</para>
    /// </summary>
    Capslock(4194329), // 0x0000000000400019
    /// <summary>
    /// <para>Num Lock key.</para>
    /// </summary>
    Numlock(4194330), // 0x000000000040001A
    /// <summary>
    /// <para>Scroll Lock key.</para>
    /// </summary>
    Scrolllock(4194331), // 0x000000000040001B
    /// <summary>
    /// <para>F1 key.</para>
    /// </summary>
    F1(4194332), // 0x000000000040001C
    /// <summary>
    /// <para>F2 key.</para>
    /// </summary>
    F2(4194333), // 0x000000000040001D
    /// <summary>
    /// <para>F3 key.</para>
    /// </summary>
    F3(4194334), // 0x000000000040001E
    /// <summary>
    /// <para>F4 key.</para>
    /// </summary>
    F4(4194335), // 0x000000000040001F
    /// <summary>
    /// <para>F5 key.</para>
    /// </summary>
    F5(4194336), // 0x0000000000400020
    /// <summary>
    /// <para>F6 key.</para>
    /// </summary>
    F6(4194337), // 0x0000000000400021
    /// <summary>
    /// <para>F7 key.</para>
    /// </summary>
    F7(4194338), // 0x0000000000400022
    /// <summary>
    /// <para>F8 key.</para>
    /// </summary>
    F8(4194339), // 0x0000000000400023
    /// <summary>
    /// <para>F9 key.</para>
    /// </summary>
    F9(4194340), // 0x0000000000400024
    /// <summary>
    /// <para>F10 key.</para>
    /// </summary>
    F10(4194341), // 0x0000000000400025
    /// <summary>
    /// <para>F11 key.</para>
    /// </summary>
    F11(4194342), // 0x0000000000400026
    /// <summary>
    /// <para>F12 key.</para>
    /// </summary>
    F12(4194343), // 0x0000000000400027
    /// <summary>
    /// <para>F13 key.</para>
    /// </summary>
    F13(4194344), // 0x0000000000400028
    /// <summary>
    /// <para>F14 key.</para>
    /// </summary>
    F14(4194345), // 0x0000000000400029
    /// <summary>
    /// <para>F15 key.</para>
    /// </summary>
    F15(4194346), // 0x000000000040002A
    /// <summary>
    /// <para>F16 key.</para>
    /// </summary>
    F16(4194347), // 0x000000000040002B
    /// <summary>
    /// <para>F17 key.</para>
    /// </summary>
    F17(4194348), // 0x000000000040002C
    /// <summary>
    /// <para>F18 key.</para>
    /// </summary>
    F18(4194349), // 0x000000000040002D
    /// <summary>
    /// <para>F19 key.</para>
    /// </summary>
    F19(4194350), // 0x000000000040002E
    /// <summary>
    /// <para>F20 key.</para>
    /// </summary>
    F20(4194351), // 0x000000000040002F
    /// <summary>
    /// <para>F21 key.</para>
    /// </summary>
    F21(4194352), // 0x0000000000400030
    /// <summary>
    /// <para>F22 key.</para>
    /// </summary>
    F22(4194353), // 0x0000000000400031
    /// <summary>
    /// <para>F23 key.</para>
    /// </summary>
    F23(4194354), // 0x0000000000400032
    /// <summary>
    /// <para>F24 key.</para>
    /// </summary>
    F24(4194355), // 0x0000000000400033
    /// <summary>
    /// <para>F25 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F25(4194356), // 0x0000000000400034
    /// <summary>
    /// <para>F26 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F26(4194357), // 0x0000000000400035
    /// <summary>
    /// <para>F27 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F27(4194358), // 0x0000000000400036
    /// <summary>
    /// <para>F28 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F28(4194359), // 0x0000000000400037
    /// <summary>
    /// <para>F29 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F29(4194360), // 0x0000000000400038
    /// <summary>
    /// <para>F30 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F30(4194361), // 0x0000000000400039
    /// <summary>
    /// <para>F31 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F31(4194362), // 0x000000000040003A
    /// <summary>
    /// <para>F32 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F32(4194363), // 0x000000000040003B
    /// <summary>
    /// <para>F33 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F33(4194364), // 0x000000000040003C
    /// <summary>
    /// <para>F34 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F34(4194365), // 0x000000000040003D
    /// <summary>
    /// <para>F35 key. Only supported on macOS and Linux due to a Windows limitation.</para>
    /// </summary>
    F35(4194366), // 0x000000000040003E
    /// <summary>
    /// <para>Context menu key.</para>
    /// </summary>
    Menu(4194370), // 0x0000000000400042
    /// <summary>
    /// <para>Hyper key. (On Linux/X11 only).</para>
    /// </summary>
    Hyper(4194371), // 0x0000000000400043
    /// <summary>
    /// <para>Help key.</para>
    /// </summary>
    Help(4194373), // 0x0000000000400045
    /// <summary>
    /// <para>Media back key. Not to be confused with the Back button on an Android device.</para>
    /// </summary>
    Back(4194376), // 0x0000000000400048
    /// <summary>
    /// <para>Media forward key.</para>
    /// </summary>
    Forward(4194377), // 0x0000000000400049
    /// <summary>
    /// <para>Media stop key.</para>
    /// </summary>
    Stop(4194378), // 0x000000000040004A
    /// <summary>
    /// <para>Media refresh key.</para>
    /// </summary>
    Refresh(4194379), // 0x000000000040004B
    /// <summary>
    /// <para>Volume down key.</para>
    /// </summary>
    Volumedown(4194380), // 0x000000000040004C
    /// <summary>
    /// <para>Mute volume key.</para>
    /// </summary>
    Volumemute(4194381), // 0x000000000040004D
    /// <summary>
    /// <para>Volume up key.</para>
    /// </summary>
    Volumeup(4194382), // 0x000000000040004E
    /// <summary>
    /// <para>Media play key.</para>
    /// </summary>
    Mediaplay(4194388), // 0x0000000000400054
    /// <summary>
    /// <para>Media stop key.</para>
    /// </summary>
    Mediastop(4194389), // 0x0000000000400055
    /// <summary>
    /// <para>Previous song key.</para>
    /// </summary>
    Mediaprevious(4194390), // 0x0000000000400056
    /// <summary>
    /// <para>Next song key.</para>
    /// </summary>
    Medianext(4194391), // 0x0000000000400057
    /// <summary>
    /// <para>Media record key.</para>
    /// </summary>
    Mediarecord(4194392), // 0x0000000000400058
    /// <summary>
    /// <para>Home page key.</para>
    /// </summary>
    Homepage(4194393), // 0x0000000000400059
    /// <summary>
    /// <para>Favorites key.</para>
    /// </summary>
    Favorites(4194394), // 0x000000000040005A
    /// <summary>
    /// <para>Search key.</para>
    /// </summary>
    Search(4194395), // 0x000000000040005B
    /// <summary>
    /// <para>Standby key.</para>
    /// </summary>
    Standby(4194396), // 0x000000000040005C
    /// <summary>
    /// <para>Open URL / Launch Browser key.</para>
    /// </summary>
    Openurl(4194397), // 0x000000000040005D
    /// <summary>
    /// <para>Launch Mail key.</para>
    /// </summary>
    Launchmail(4194398), // 0x000000000040005E
    /// <summary>
    /// <para>Launch Media key.</para>
    /// </summary>
    Launchmedia(4194399), // 0x000000000040005F
    /// <summary>
    /// <para>Launch Shortcut 0 key.</para>
    /// </summary>
    Launch0(4194400), // 0x0000000000400060
    /// <summary>
    /// <para>Launch Shortcut 1 key.</para>
    /// </summary>
    Launch1(4194401), // 0x0000000000400061
    /// <summary>
    /// <para>Launch Shortcut 2 key.</para>
    /// </summary>
    Launch2(4194402), // 0x0000000000400062
    /// <summary>
    /// <para>Launch Shortcut 3 key.</para>
    /// </summary>
    Launch3(4194403), // 0x0000000000400063
    /// <summary>
    /// <para>Launch Shortcut 4 key.</para>
    /// </summary>
    Launch4(4194404), // 0x0000000000400064
    /// <summary>
    /// <para>Launch Shortcut 5 key.</para>
    /// </summary>
    Launch5(4194405), // 0x0000000000400065
    /// <summary>
    /// <para>Launch Shortcut 6 key.</para>
    /// </summary>
    Launch6(4194406), // 0x0000000000400066
    /// <summary>
    /// <para>Launch Shortcut 7 key.</para>
    /// </summary>
    Launch7(4194407), // 0x0000000000400067
    /// <summary>
    /// <para>Launch Shortcut 8 key.</para>
    /// </summary>
    Launch8(4194408), // 0x0000000000400068
    /// <summary>
    /// <para>Launch Shortcut 9 key.</para>
    /// </summary>
    Launch9(4194409), // 0x0000000000400069
    /// <summary>
    /// <para>Launch Shortcut A key.</para>
    /// </summary>
    Launcha(4194410), // 0x000000000040006A
    /// <summary>
    /// <para>Launch Shortcut B key.</para>
    /// </summary>
    Launchb(4194411), // 0x000000000040006B
    /// <summary>
    /// <para>Launch Shortcut C key.</para>
    /// </summary>
    Launchc(4194412), // 0x000000000040006C
    /// <summary>
    /// <para>Launch Shortcut D key.</para>
    /// </summary>
    Launchd(4194413), // 0x000000000040006D
    /// <summary>
    /// <para>Launch Shortcut E key.</para>
    /// </summary>
    Launche(4194414), // 0x000000000040006E
    /// <summary>
    /// <para>Launch Shortcut F key.</para>
    /// </summary>
    Launchf(4194415), // 0x000000000040006F
    /// <summary>
    /// <para>"Globe" key on Mac / iPad keyboard.</para>
    /// </summary>
    Globe(4194416), // 0x0000000000400070
    /// <summary>
    /// <para>"On-screen keyboard" key iPad keyboard.</para>
    /// </summary>
    Keyboard(4194417), // 0x0000000000400071
    /// <summary>
    /// <para>英数 key on Mac keyboard.</para>
    /// </summary>
    JisEisu(4194418), // 0x0000000000400072
    /// <summary>
    /// <para>かな key on Mac keyboard.</para>
    /// </summary>
    JisKana(4194419), // 0x0000000000400073
    /// <summary>
    /// <para>Multiply (*) key on the numeric keypad.</para>
    /// </summary>
    KpMultiply(4194433), // 0x0000000000400081
    /// <summary>
    /// <para>Divide (/) key on the numeric keypad.</para>
    /// </summary>
    KpDivide(4194434), // 0x0000000000400082
    /// <summary>
    /// <para>Subtract (-) key on the numeric keypad.</para>
    /// </summary>
    KpSubtract(4194435), // 0x0000000000400083
    /// <summary>
    /// <para>Period (.) key on the numeric keypad.</para>
    /// </summary>
    KpPeriod(4194436), // 0x0000000000400084
    /// <summary>
    /// <para>Add (+) key on the numeric keypad.</para>
    /// </summary>
    KpAdd(4194437), // 0x0000000000400085
    /// <summary>
    /// <para>Number 0 on the numeric keypad.</para>
    /// </summary>
    Kp0(4194438), // 0x0000000000400086
    /// <summary>
    /// <para>Number 1 on the numeric keypad.</para>
    /// </summary>
    Kp1(4194439), // 0x0000000000400087
    /// <summary>
    /// <para>Number 2 on the numeric keypad.</para>
    /// </summary>
    Kp2(4194440), // 0x0000000000400088
    /// <summary>
    /// <para>Number 3 on the numeric keypad.</para>
    /// </summary>
    Kp3(4194441), // 0x0000000000400089
    /// <summary>
    /// <para>Number 4 on the numeric keypad.</para>
    /// </summary>
    Kp4(4194442), // 0x000000000040008A
    /// <summary>
    /// <para>Number 5 on the numeric keypad.</para>
    /// </summary>
    Kp5(4194443), // 0x000000000040008B
    /// <summary>
    /// <para>Number 6 on the numeric keypad.</para>
    /// </summary>
    Kp6(4194444), // 0x000000000040008C
    /// <summary>
    /// <para>Number 7 on the numeric keypad.</para>
    /// </summary>
    Kp7(4194445), // 0x000000000040008D
    /// <summary>
    /// <para>Number 8 on the numeric keypad.</para>
    /// </summary>
    Kp8(4194446), // 0x000000000040008E
    /// <summary>
    /// <para>Number 9 on the numeric keypad.</para>
    /// </summary>
    Kp9(4194447), // 0x000000000040008F
    /// <summary>
    /// <para>Unknown key.</para>
    /// </summary>
    Unknown(8388607), // 0x00000000007FFFFF
    ;
    @Override
    public int value() {
        return value;
    }

    private final int value;

    Key(int v){
        value = v;
    }
}
