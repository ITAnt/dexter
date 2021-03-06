.class public LExceptionTestExerciser;
.super LTestExerciser;

# instance fields
.field private final test:LExceptionTest;

# direct methods
.method public constructor <init>(LExceptionTest;)V
    .registers 2

    invoke-direct {p0}, LTestExerciser;-><init>()V

    iput-object p1, p0, LExceptionTestExerciser;->test:LExceptionTest;
    return-void

.end method

.method private static run(LExceptionTest;Ljava/lang/Object;)Ljava/lang/Object;
    .registers 7

    const-string v2, "DexterTest"

    :try_start
        invoke-virtual {p0, p1}, LExceptionTest;->execute(Ljava/lang/Object;)V
    :try_end
    .catchall {:try_start .. :try_end} :handler

    # SUCCESS  => return NULL

    const-string v3, "... didn't throw an exception"
    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    const/4 v0, 0x0
    return-object v0

    # EXCEPTION  => return ex

    :handler
    move-exception v1

    const-string v3, "... threw an exception"
    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    invoke-virtual {v1}, Ljava/lang/Throwable;->printStackTrace()V    

    return-object v1

.end method

# virtual methods
.method public run()Z
    .registers 7

    const-string v5, "DexterTest"

    # get the test = v0
    iget-object v0, p0, LExceptionTestExerciser;->test:LExceptionTest;

    const-string v6, "EXCEPTION TEST"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    invoke-interface {v0}, LTest;->getName()Ljava/lang/String;
    move-result-object v6
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    # get the expected outcome = v1
    invoke-virtual {v0}, LExceptionTest;->expected()Ljava/lang/Class;
    move-result-object v1

    # get the argument = v2
    invoke-virtual {v0}, LExceptionTest;->arg()Ljava/lang/Object;
    move-result-object v2

    # run with the argument => v3
    const-string v6, "Running untainted..."
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-static {v0, v2}, LExceptionTestExerciser;->run(LExceptionTest;Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3

    # now taint the argument
    invoke-static {v2}, LTaintUtils;->taint(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2

    # run with the tainted argument => v4
    const-string v6, "Running tainted..."
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-static {v0, v2}, LExceptionTestExerciser;->run(LExceptionTest;Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v4

    const-string v6, "Testing..."
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz v1, :no_taint_test

    # are they tainted?
    const-string v6, " - first outcome not tainted"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    invoke-static {v3}, LTaintUtils;->isTainted(Ljava/lang/Object;)Z
    move-result v0
    if-nez v0, :return_false

    const-string v6, " - second outcome tainted"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    invoke-static {v4}, LTaintUtils;->isTainted(Ljava/lang/Object;)Z
    move-result v0
    if-eqz v0, :return_false

    # both must match the expected outcome
    const-string v6, " - first outcome correct"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-virtual {v3}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v3
    if-ne v1, v3, :return_false

    const-string v6, " - second outcome correct"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    invoke-virtual {v4}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v4
    if-ne v1, v4, :return_false

    goto :return_true

    :no_taint_test

    # both must match the expected outcome
    const-string v6, " - first outcome correct"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-ne v1, v3, :return_false

    const-string v6, " - second outcome correct"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    if-ne v1, v4, :return_false

    :return_true
    const/4 v0, 0x1
    return v0

    :return_false
    const-string v6, "... FAIL"
    invoke-static {v5, v6}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
    const/4 v0, 0x0
    return v0

.end method

.method public getTest()LTest;
    .registers 2

    iget-object v0, p0, LExceptionTestExerciser;->test:LExceptionTest;
    return-object v0

.end method
