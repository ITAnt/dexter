.class public Luk/ac/cam/db538/dexter/tests/Test_InstanceField_InternalClass_InheritedField;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: int. class, inherited field"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "real field owner should be recognized at parsing"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 7

    # create two objects
    new-instance v2, Luk/ac/cam/db538/dexter/tests/MyClass_Point;
    invoke-direct {v2}, Luk/ac/cam/db538/dexter/tests/MyClass_Point;-><init>()V
    new-instance v3, Luk/ac/cam/db538/dexter/tests/MyClass_Point;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/MyClass_Point;-><init>()V

    # propagate directly
    iput p1, v2, Luk/ac/cam/db538/dexter/tests/MyClass_Point;->x:I
    iget v1, v2, Luk/ac/cam/db538/dexter/tests/MyClass_Point;->x:I

    # propagate from inside the inheriting class
    invoke-virtual {v3, v1}, Luk/ac/cam/db538/dexter/tests/MyClass_Point;->setX(I)V
    invoke-virtual {v3}, Luk/ac/cam/db538/dexter/tests/MyClass_Point;->getX()I
    move-result v0

    return v0
    
.end method
