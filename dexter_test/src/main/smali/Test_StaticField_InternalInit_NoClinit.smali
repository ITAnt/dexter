.class public LTest_StaticField_InternalInit_NoClinit;
.super LNoExceptionTest;

# instance fields
.field private static X:Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, LNoExceptionTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: internal init, no <clinit>"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "forces creation of <clinit>"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    # read the contents of the uninitialized field => NULL
    sget-object v0, LTest_StaticField_InternalInit_NoClinit;->X:Ljava/lang/Object;

    # v1 = new ArrayList()
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    # v1.add(v0) => calls t0.get()
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    return-void
    
.end method
