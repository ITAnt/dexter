.class public LTest_Compare_Arg1;
.super LPropagationTest;



# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Compare: arg1, float"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "cmpl-float rX, [+], rConst"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 4

    int-to-float v1, p1
    const/4 v2, 0x0
    cmpl-float v0, v1, v2
    return v0
    
.end method
