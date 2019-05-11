#include <stdio.h>
#include <stdbool.h>

typedef struct ClassTest{
    bool(*AreTheyTheSame)(struct ClassTest*,int,int);
}ClassTest;
bool ClassTest_AreTheyTheSame (ClassTest* this,int x,int y){
    return (x)==(y);
}
void init_ClassTest(ClassTest* input){
    input->AreTheyTheSame = &ClassTest_AreTheyTheSame;
}
typedef struct OtherClass{
    bool(*AreTheyDifferent)(struct OtherClass*,int,int);
}OtherClass;
bool OtherClass_AreTheyDifferent (OtherClass* this,int x,int y){
    return (x)!=(y);
}
void init_OtherClass(OtherClass* input){
    input->AreTheyDifferent = &OtherClass_AreTheyDifferent;
}
typedef struct Test{
    int(*main)(struct Test*);
}Test;
int Test_main (Test* this){
    struct ClassTest *foo, fooOriginal; foo = &fooOriginal; init_ClassTest(foo);
    struct OtherClass *bar, barOriginal; bar = &barOriginal; init_OtherClass(bar);
    if ((foo->AreTheyTheSame(foo, 1, 1))&&(bar->AreTheyDifferent(bar, 1, 2))){
        printf("%s\n", "Success!");
    }
    else{
        printf("%s\n", "Failure!");
    };
}
void init_Test(Test* input){
    input->main = &Test_main;
}
int main(int argc, char** argv){
    Test mainClass = {};
    init_Test(&mainClass);
    return Test_main(&mainClass);
}
