.class public LTest_InstanceField_Primitive;
.super LPropagationTest;


# instance fields
.field private X:I

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: primitive"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "this.X = [+]; return this.X;"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    iput p1, p0, LTest_InstanceField_Primitive;->X:I
    iget v0, p0, LTest_InstanceField_Primitive;->X:I

    return v0
    
.end method
