.class public LTest_ExternalCall_Exception;
.super LExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "External call: exception"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "IndexOutOfBounds on ArrayList"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 7

    check-cast p1, Ljava/util/ArrayList;
    const/4 v0, 4
    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;
    return-void

.end method

.method public expected()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/IndexOutOfBoundsException;
    return-object v0

.end method

.method public arg()Ljava/lang/Object;
    .registers 1

    new-instance v0, Ljava/util/ArrayList;
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V
    return-object v0

.end method