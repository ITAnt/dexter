.class public LMyClass_Point;
.super Landroid/graphics/Point;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Landroid/graphics/Point;-><init>()V
    return-void
    
.end method

.method public getX()I
    .registers 1

    iget v0, p0, LMyClass_Point;->x:I
    return v0
    
.end method

.method public setX(I)V
    .registers 2

    iput p1, p0, LMyClass_Point;->x:I
    return-void
    
.end method

.method public equals(Ljava/lang/Object;)Z
    .registers 2

    const/4 v0, 1
    return v0

.end method
