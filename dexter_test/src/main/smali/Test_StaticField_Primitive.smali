.class public LTest_StaticField_Primitive;
.super LPropagationTest;


# instance fields
.field private static X:I

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: primitive"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = [+]; return Test.X;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    sput p1, LTest_StaticField_Primitive;->X:I
    sget v0, LTest_StaticField_Primitive;->X:I

    return v0
    
.end method
