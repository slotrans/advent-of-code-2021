{$R+} { enable range checking }
{$I+} { enable I/O checking }
program puzzle02;

var
    dummy: string[1];
    input02: text; { file }
    {idx: integer;}
    instruction: string[16];
    inst_value: integer;
    errCode: integer;
    position: longint;
    depth: longint;
    aim: longint;
    p1_answer: longint;
    p2_answer: longint;

begin
    position := 0;
    depth := 0;

    assign(input02, 'INPUT02.TXT');
    reset(input02);
    while not eof(input02) do
    begin
        readln(input02, instruction);

        { we know that lines are always of one of the forms...
            'forward X'
            'down X'
            'up X'
          so instead of hunting for the space and splitting on it,
          just test the first character and then parse the number
          from where we know it starts
        }
        case instruction[1] of
            'f': begin
                val(copy(instruction, 8, 16), inst_value, errCode);
                position := position + inst_value;
            end;
            'd': begin
                val(copy(instruction, 6, 16), inst_value, errCode);
                depth := depth + inst_value;
            end;
            'u': begin
                val(copy(instruction, 4, 16), inst_value, errCode);
                depth := depth - inst_value;
            end;
        end;
    end;
    close(input02);

    write('position=');
    write(position); { 2003 }
    write(', depth=');
    writeln(depth); { 980 }
    p1_answer := position * depth;
    write('(p1 answer) position * depth = '); { 1962940 }
    writeln(p1_answer);

    { -- part 2 -- }

    position := 0;
    depth := 0;
    aim := 0;

    assign(input02, 'INPUT02.TXT');
    reset(input02);
    while not eof(input02) do
    begin
        readln(input02, instruction);

        case instruction[1] of
            'd': begin
                val(copy(instruction, 6, 16), inst_value, errCode);
                aim := aim + inst_value;
            end;
            'u': begin
                val(copy(instruction, 4, 16), inst_value, errCode);
                aim := aim - inst_value;
            end;
            'f': begin
                val(copy(instruction, 8, 16), inst_value, errCode);
                position := position + inst_value;
                depth := depth + (aim * inst_value);
            end;
        end;
    end;
    close(input02);

    write('position=');
    write(position); { 2003 }
    write(', depth=');
    write(depth); { 905474 }
    write(', aim=');
    writeln(aim); { 980 }
    p2_answer := position * depth;
    write('(p2 answer) position * depth = ');
    writeln(p2_answer); { 1813664422 }

    writeln('press enter...');
    readln(dummy);
end.