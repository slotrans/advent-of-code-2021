{$R+} { enable range checking }
{$I+} { enable I/O checking}
program puzzle03;

const
    inputLength = 12;

var
    input03: text;
    bitString: string[inputLength];
    bitFreqArray: array[1..inputLength] of integer;
    mostCommonBits: string[inputLength];
    leastCommonBits: string[inputLength];
    idx: integer;
    inputCount: integer;
    gammaRate: longint;
    epsilonRate: longint;
    powerConsumption: longint;
    errCode: integer;

begin
    for idx := 1 to 12 do
        bitFreqArray[idx] := 0;
    inputCount := 0;

    assign(input03, 'input03');
    reset(input03);
    while not eof(input03) do
    begin
        readln(input03, bitString);
        for idx := 1 to inputLength do
        begin
            if bitString[idx] = '1' then
                bitFreqArray[idx] := bitFreqArray[idx] + 1;
        end;
        inputCount := inputCount + 1;
    end;
    close(input03);

    mostCommonBits := '';
    leastCommonBits := '';
    for idx := 1 to inputLength do
    begin
        if bitFreqArray[idx] / inputCount >= 0.5 then
        begin
            {mostCommonBits[idx] := '1'}
            mostCommonBits := concat(mostCommonBits, '1'); { setting individual characters ala mostCommonBits[idx] = '1' appears not to work}
            leastCommonBits := concat(leastCommonBits, '0');
        end
        else
        begin
            {mostCommonBits[idx] := '0';}
            mostCommonBits := concat(mostCommonBits, '0');
            leastCommonBits := concat(leastCommonBits, '1');
        end;
    end;

    write('most common bits: ');
    writeln(mostCommonBits);
    {bitStringForParsing := concat('%', mostCommonBits);
    writeln(bitStringForParsing);}
    val(concat('%', mostCommonBits), gammaRate, errCode);
    write('gamma rate: ');
    writeln(gammaRate); { 199 }
    val(concat('%', leastCommonBits), epsilonRate, errCode);
    write('epsilon rate: ');
    writeln(epsilonRate); { 3896 }
    powerConsumption := gammaRate * epsilonRate;
    write('(p1 answer) power consumption: ');
    writeln(powerConsumption); { 775304 }
end.

