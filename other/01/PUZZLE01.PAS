{$R+} { enable range checking }
{$I+} { enable I/O checking }
program aoc2021_01;

const
    file_size = 2000;

var
    dummy: string[1];
    input01: text; { `text` is a kind of file }
    measurements: array[0..file_size-1] of integer;
    idx: integer;
    depth: integer;
    last_depth: integer;
    increases: integer;

begin
    writeln('reading input...');
    assign(input01, 'INPUT01.TXT');
    reset(input01);
    idx := 0;
    while not eof(input01) do
    begin
        readln(input01, measurements[idx]);
        {writeln(measurements[idx]);}
        idx := idx + 1;
    end;
    close(input01);

    increases := 0;
    last_depth := measurements[0];
    for idx := 1 to file_size-1 do
    begin
        depth := measurements[idx];
        if depth > last_depth then
            increases := increases + 1;
        last_depth := depth;
    end;

    write('(p1 answer) increases = ');
    writeln(increases); { 1548 }

    increases := 0;
    last_depth := measurements[0] + measurements[1] + measurements[2];
    for idx := 1 to file_size-3 do
    begin
        depth := measurements[idx] + measurements[idx+1] + measurements[idx+2];
        if depth > last_depth then
            increases := increases + 1;
        last_depth := depth;
    end;

    write('(p2 answer) increases = ');
    writeln(increases); { 1589 }

    writeln('press enter...');
    readln(dummy);
end.