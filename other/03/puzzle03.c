#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define BIT_STRING_LENGTH 12


typedef struct bitstring_list_node_t bitstring_list_node_t;
struct bitstring_list_node_t
{
    char* bitstring;
    bitstring_list_node_t* next;
};

typedef struct
{
    size_t size;
    bitstring_list_node_t* head;
    bitstring_list_node_t* tail;
} bitstring_list_t;


char* bitstring_list_get(const bitstring_list_t* list, size_t index)
{
    if(index >= list->size)
    {
        fprintf(stderr, "ERR: bitstring_list_get out-of-bounds! size=%d, requested index=%d\n", list->size, index);
        return NULL;
    }

    bitstring_list_node_t* temp = list->head;
    for(size_t i = 0; i < index; ++i)
    {
        temp = temp->next;
    }

    return temp->bitstring;
}


bitstring_list_t* bitstring_list_new()
{
    bitstring_list_t* list = malloc(sizeof(bitstring_list_t));
    list->head = NULL;
    list->tail = NULL;
    list->size = 0;

    return list;
}


void bitstring_list_delete(bitstring_list_t* list)
{
    //printf("bitstring_list_delete!\n");
    bitstring_list_node_t* elem = list->head;
    size_t i = 0;
    while(elem != NULL)
    {
        //printf("freeing node %d\n", i);
        bitstring_list_node_t* next = elem->next;
        free(elem->bitstring);
        free(elem);

        elem = next;
        ++i;
    }
    //printf("freeing list struct\n");
    free(list);
}


void bitstring_list_append(bitstring_list_t* const list, const char* bitstring)
{
    bitstring_list_node_t* tail = list->tail;

    bitstring_list_node_t* new_tail = malloc(sizeof(bitstring_list_node_t));
    //new_tail->bitstring = bitstring;
    new_tail->bitstring = malloc(sizeof(char) * (strlen(bitstring)+1));
    strcpy(new_tail->bitstring, bitstring);
    new_tail->next = NULL;

    if(list->size == 0) // empty list case
    {
        list->head = new_tail;
        list->tail = new_tail;
        list->size = 1;
    }
    else
    {
        tail->next = new_tail;

        list->tail = new_tail;
        list->size += 1;
    }
}


void bitstring_list_insert(bitstring_list_t* const list, const char* bitstring, size_t index)
{
    if(index >= (list->size)+1)
    {
        fprintf(stderr, "ERR: bitstring_list_insert out-of-bounds! size=%d, requested index=%d\n", list->size, index);
        return;
    }

    bitstring_list_node_t* new_elem = malloc(sizeof(bitstring_list_node_t));
    //new_elem->bitstring = bitstring;
    new_elem->bitstring = malloc(sizeof(char) * (strlen(bitstring)+1));
    strcpy(new_elem->bitstring, bitstring);

    if(index == 0) // inserting at head special case
    {
        new_elem->next = list->head;
        list->head = new_elem;
        list->size += 1;
    }
    else if(index == list->size)
    {
        bitstring_list_append(list, bitstring);
    }
    else
    {
        bitstring_list_node_t* temp_nminusone = list->head;
        bitstring_list_node_t* temp_n = list->head->next;
        for(size_t n = 1; n < index; ++n)
        {
            temp_nminusone = temp_nminusone->next;
            temp_n = temp_n->next;
        }

        temp_nminusone->next = new_elem;
        new_elem->next = temp_n;

        list->size += 1;
    }
}


void bitstring_list_remove(bitstring_list_t* const list, size_t index)
{
    if(index >= list->size || list->size == 0)
    {
        fprintf(stderr, "ERR: bitstring_list_insert out-of-bounds! size=%d, requested index=%d\n", list->size, index);
        return;
    }

    if(index == 0) // head case
    {
        bitstring_list_node_t* new_head = list->head->next;
        free(list->head->bitstring);
        free(list->head);
        list->head = new_head;
    }
    else
    {
        bitstring_list_node_t* temp_nminusone = list->head;
        bitstring_list_node_t* temp_n = list->head->next;
        for(size_t n = 1; n < index; ++n)
        {
            temp_nminusone = temp_nminusone->next;
            temp_n = temp_n->next;
        }

        temp_nminusone->next = temp_n->next;
        free(temp_n->bitstring);
        free(temp_n);

        if(index == (list->size)-1) // we just removed the tail
        {
            list->tail = temp_nminusone;
        }
    }
    list->size -= 1;
}


bitstring_list_t* bitstring_list_copy(const bitstring_list_t* source_list) // performs a deep copy because of how _append() works
{
    bitstring_list_t* dest_list = bitstring_list_new();

    bitstring_list_node_t* temp = source_list->head;
    while(temp != NULL)
    {
        bitstring_list_append(dest_list, temp->bitstring);

        temp = temp->next;
    }

    return dest_list;
}


bitstring_list_t* read_input(const char* file_name)
{
    // https://stackoverflow.com/questions/3501338/c-read-file-line-by-line
    FILE* file_handle;
    char* line = NULL;
    size_t len = 0;
    ssize_t bytes_read;

    file_handle = fopen(file_name, "r");
    if(file_handle == NULL)
    {
        exit(EXIT_FAILURE);
    }

    bitstring_list_t* list_from_input = bitstring_list_new();
    while((bytes_read = getline(&line, &len, file_handle)) != -1)
    {
        bitstring_list_append(list_from_input, line);
    }

    return list_from_input;
}

void test_list()
{
    printf("creating list...\n");
    bitstring_list_t* my_bitstring_list = bitstring_list_new();
    bitstring_list_append(my_bitstring_list, "foo");
    bitstring_list_append(my_bitstring_list, "bar");
    bitstring_list_append(my_bitstring_list, "zzz");

    printf("0: %s\n", bitstring_list_get(my_bitstring_list, 0));
    printf("1: %s\n", bitstring_list_get(my_bitstring_list, 1));
    printf("2: %s\n", bitstring_list_get(my_bitstring_list, 2));

    printf("adding 'flerb' at 3...\n");
    bitstring_list_insert(my_bitstring_list, "flerb", 3);
    printf("0: %s\n", bitstring_list_get(my_bitstring_list, 0));
    printf("1: %s\n", bitstring_list_get(my_bitstring_list, 1));
    printf("2: %s\n", bitstring_list_get(my_bitstring_list, 2));
    printf("3: %s\n", bitstring_list_get(my_bitstring_list, 3));

    size_t index_to_remove = 2;
    printf("removing index %d...\n", index_to_remove);
    bitstring_list_remove(my_bitstring_list, index_to_remove);
    printf("0: %s\n", bitstring_list_get(my_bitstring_list, 0));
    printf("1: %s\n", bitstring_list_get(my_bitstring_list, 1));
    printf("2: %s\n", bitstring_list_get(my_bitstring_list, 2));

    printf("deleting list...\n");
    bitstring_list_delete(my_bitstring_list);
}

void get_most_common_bits(bitstring_list_t* const list, char* most_common_bits_out)
{
    int frequencies[BIT_STRING_LENGTH] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    bitstring_list_node_t* elem = list->head;
    size_t i = 0;
    while(elem != NULL)
    {
        for(size_t bit_pos = 0; bit_pos < BIT_STRING_LENGTH; ++bit_pos)
        {
            frequencies[bit_pos] += (elem->bitstring[bit_pos] - '0'); //ascii tricks
        }

        elem = elem->next;
        ++i;
    }

    for(size_t bit_pos = 0; bit_pos < BIT_STRING_LENGTH; ++bit_pos)
    {
        if((float)frequencies[bit_pos] / list->size >= 0.5)
        {
            most_common_bits_out[bit_pos] = '1';
        }
        else
        {
            most_common_bits_out[bit_pos] = '0';
        }
    }
}

void invert_bit_string(char* const bitstring)
{
    size_t input_length = strlen(bitstring);
    for(size_t i = 0; i < input_length; ++i)
    {
        if(bitstring[i] == '0')
        {
            bitstring[i] = '1';
        }
        else if(bitstring[i] == '1')
        {
            bitstring[i] = '0';
        }
    }
}

void filter_by_rule(bitstring_list_t* const list, const char* bit_rule, size_t bit_pos)
{
    size_t index = 0;
    bitstring_list_node_t* temp = list->head;
    while(temp != NULL)
    {
        bitstring_list_node_t* saved_next = temp->next;

        if(temp->bitstring[bit_pos] != bit_rule[bit_pos])
        {
            bitstring_list_remove(list, index);
        }
        else
        {
            // only advance index if we *didn't* remove the current element
            ++index;
        }

        temp = saved_next;
    }
}


int main()
{
    printf("reading input...\n");
    bitstring_list_t* input03 = read_input("input03");

    printf("Part 1:\n");

    printf("getting most common bits...\n");
    char most_common_bits[] = "____________";
    get_most_common_bits(input03, most_common_bits);
    printf("most common bits: %s\n", most_common_bits);

    printf("getting least common bits...\n");
    char least_common_bits[] = "____________";
    get_most_common_bits(input03, least_common_bits);
    invert_bit_string(least_common_bits);
    printf("least common bits: %s\n", least_common_bits);

    int32_t gamma_rate = strtol(most_common_bits, NULL, 2);
    int32_t epsilon_rate = strtol(least_common_bits, NULL, 2);

    printf("gamma rate = %d\n", gamma_rate); // 199
    printf("epsilon rate = %d\n", epsilon_rate); // 3896

    int32_t power_consumption = gamma_rate * epsilon_rate;
    printf("(p1 answer) power consumption = %d\n", power_consumption); // 775304


    /* part 2 */
    printf("\nPart 2:\n");

    bitstring_list_t* temp_bit_strings = bitstring_list_copy(input03);
    for(size_t i = 0; i < BIT_STRING_LENGTH; ++i)
    {
        char mcb[] = "____________";
        get_most_common_bits(temp_bit_strings, mcb);

        filter_by_rule(temp_bit_strings, mcb, i);
        if(temp_bit_strings->size == 1)
        {
            break;
        }
    }
    //printf("%s\n", temp_bit_strings->head->bitstring);
    int32_t oxygen_generator_rating = strtol(temp_bit_strings->head->bitstring, NULL, 2);
    printf("oxygen generator rating = %d\n", oxygen_generator_rating); // 509


    bitstring_list_delete(temp_bit_strings);
    temp_bit_strings = bitstring_list_copy(input03);
    for(size_t i = 0; i < BIT_STRING_LENGTH; ++i)
    {
        char lcb[] = "____________";
        get_most_common_bits(temp_bit_strings, lcb);
        invert_bit_string(lcb);

        filter_by_rule(temp_bit_strings, lcb, i);
        if(temp_bit_strings->size == 1)
        {
            break;
        }
    }
    //printf("%s\n", temp_bit_strings->head->bitstring);
    int32_t co2_scrubber_rating = strtol(temp_bit_strings->head->bitstring, NULL, 2);
    printf("co2 scrubber rating = %d\n", co2_scrubber_rating); // 2693

    bitstring_list_delete(temp_bit_strings);

    int32_t life_support_rating = oxygen_generator_rating * co2_scrubber_rating;
    printf("(p2 answer) life support rating = %d\n", life_support_rating); // 1370737


    /* clean up and exit */
    bitstring_list_delete(input03);
    return 0;
}
