.class public Luk/ac/cam/db538/dexter/tests/Test_StaticField_External;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# instance fields
.field private static X:Ljava/util/concurrent/Semaphore;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: external"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Test.X = new Semaphore([+]); return Test.X.getQueueLength();"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, Ljava/util/concurrent/Semaphore;
    invoke-direct {v2, p1}, Ljava/util/concurrent/Semaphore;-><init>(I)V

    # propagate
    sput-object v2, Luk/ac/cam/db538/dexter/tests/Test_StaticField_External;->X:Ljava/util/concurrent/Semaphore;
    sget-object v1, Luk/ac/cam/db538/dexter/tests/Test_StaticField_External;->X:Ljava/util/concurrent/Semaphore;

    # retrieve some primitive from the object
    invoke-virtual {v1}, Ljava/util/concurrent/Semaphore;->getQueueLength()I
    move-result v0

    return v0
    
.end method
