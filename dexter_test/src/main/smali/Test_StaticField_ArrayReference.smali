.class public Luk/ac/cam/db538/dexter/tests/Test_StaticField_ArrayReference;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# static fields
.field private static X:[Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: references array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = new Object[[+]]; return Test.X.length;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # size mod 4
    rem-int/lit8 p1, p1, 4

    # create object
    new-array v2, p1, [Ljava/lang/Object;

    # propagate
    sput-object v2, Luk/ac/cam/db538/dexter/tests/Test_StaticField_ArrayReference;->X:[Ljava/lang/Object;
    sget-object v1, Luk/ac/cam/db538/dexter/tests/Test_StaticField_ArrayReference;->X:[Ljava/lang/Object;

    # retrieve some primitive from the object
    array-length v0, v1

    return v0
    
.end method
