{$R+} { enable range checking }
{$I+} { enable I/O checking}
program puzzle03;

const
    inputLength = 12; { hard-coding observations about the input! }
    inputCount = 1000; { same! }

type
    TBitString = string[inputLength];
    TBitStringList = array[1..inputCount] of TBitString;
    TBitcountArray = array[1..inputLength] of integer;


function getBitFrequency(bitStrings: TBitStringList; lastIndex: integer): TBitcountArray;
var
    i, j: integer;
    bitFreqArray: TBitcountArray;
begin
    for j := 1 to inputLength do
        bitFreqArray[j] := 0;

    for i := 1 to lastIndex do
        for j := 1 to inputLength do
            if bitStrings[i][j] = '1' then
                bitFreqArray[j] := bitFreqArray[j] + 1;

    getBitFrequency := bitFreqArray;
end;


function getMostCommonBits(bitStrings: TBitStringList; lastIndex: integer): TBitString;
var
    bitFreqArray: TBitcountArray;
    mostCommonBits: TBitString;
    idx: integer;
begin
    bitFreqArray := getBitFrequency(bitStrings, lastIndex);

    mostCommonBits := '';
    for idx := 1 to inputLength do
        if bitFreqArray[idx] / inputCount >= 0.5 then
            mostCommonBits := concat(mostCommonBits, '1') { setting individual characters ala mostCommonBits[idx] = '1' appears not to work}
        else
            mostCommonBits := concat(mostCommonBits, '0');

    getMostCommonBits := mostCommonBits;
end;


function invertBitString(inBitString: TBitString): TBitString;
var
    outBitString: TBitString;
    idx: integer;
begin
    outBitString := '';
    for idx := 1 to inputLength do
        if inBitString[idx] = '1' then
            outBitString := concat(outBitString, '0')
        else
            outBitString := concat(outBitString, '1');

    invertBitString := outBitString;
end;


{**************** main program ****************}
var
    input03: text;
    bitStrings: TBitStringList;
    mostCommonBits: string[inputLength];
    leastCommonBits: string[inputLength];
    idx: integer;
    gammaRate: longint;
    epsilonRate: longint;
    powerConsumption: longint;
    errCode: integer;

begin
    assign(input03, 'input03');
    reset(input03);
    idx := 1;
    while not eof(input03) do
    begin
        readln(input03, bitStrings[idx]);
        idx := idx + 1;
    end;
    close(input03);

    mostCommonBits := getMostCommonBits(bitStrings, inputCount);
    leastCommonBits := invertBitString(mostCommonBits);

    write('most common bits: ');
    writeln(mostCommonBits);
    val(concat('%', mostCommonBits), gammaRate, errCode);
    write('gamma rate: ');
    writeln(gammaRate); { 199 }
    val(concat('%', leastCommonBits), epsilonRate, errCode);
    write('epsilon rate: ');
    writeln(epsilonRate); { 3896 }
    powerConsumption := gammaRate * epsilonRate;
    write('(p1 answer) power consumption: ');
    writeln(powerConsumption); { 775304 }

    {-- part 2 --}


end.

