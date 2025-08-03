# BuildTheWorldForNPU

Made by Infinomat

## 公约

- 注册新东西以及存放新资源文件时，要条理清晰，把代码写到该写的地方，把东西放到该放的位置上
- 日志能加最好都加上
- 保持代码美观

## 重构结构简述

- npu
    - client 客户端入口
    - renders 渲染器
        - entity 实体的渲染器
    - blocks
        - npublocknewclasses 新方块的模板
        - NpuBlocks.class 一切新方块将在此自动注册
    - entities
        - npuentitynewclasses 新实体的模板
        - NpuEntities.class 一切新实体应该在此注册
        - NpuEntitySubscriber.class 向事件加入新实体与新渲染方式的链接
    - itemgroups
        - CreativeModeTab.class 用于向原版创造模式物品栏添加新物品
        - NpuCreativeModeTabs.class 一切新创造模式物品栏应该在此注册
    - items
        - npuitemnewclasses 新物品的模板
        - NpuItems.class 一切新物品（包括方块物品）将在此自动注册
      - util
        - register
          - data
            - template 新对象的数据
            - RegisterList.class 用于获取新对象的id与模板
          - RegisterObjects.class 创建文件并写入数据
        - FileDataGetter.class 获取文件的json数据
        - FolderDataGetter.class 获取文件夹的json数据
        - Reference.class 用于获得模组基本信息
        - PathTools.class 用于处理路径
      - NPU 主类（一般不动它）

## 如何操作

### 注册新方块

1. 判断是哪种方块

   NORMAL_STRUCTURE 普通方块

   HORIZONTAL_DIRECTIONAL_STRUCTURE 具有东南西北四个方向的方块

   HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE 具有十二个方向的方块

   NORMAL_HALF_SLAB 普通可堆叠的台阶方块

   HORIZONTAL_DIRECTIONAL_HALF_SLAB 具有东南西北四个方向的可堆叠的台阶方块

   DOOR_AND_WINDOW 具有开关两种模型的方块

   （更多类型敬请期待）
2. 判断方块材质

   IRON

   ROCK

   （更多类型敬请期待）
3. 根据所需属性在 [resources/register/npu/block/template](src/main/resources/register/npu/block/template) 中寻找一个合适的模板，如果都不合适就自己照猫画虎做一个
4. 在 [resources/register/npu/block/register](src/main/resources/register/npu/block/register) 中找到你希望加入的物品栏文件，在里面相应的模板下加入新方块的id
5. 将方块状态写到 [resources/assets/npu/blockstates](src/main/resources/assets/npu/blockstates) 中
6. 将对应物品状态写到 [resources/assets/npu/items](src/main/resources/assets/npu/items) 中
7. 在 [resources/assets/npu/models/block](src/main/resources/assets/npu/models/block) 中添加新方块的模型
8. 在 [resources/assets/npu/textures/block](src/main/resources/assets/npu/textures/block) 中添加方块的贴图
9. 根据原模组翻译文件向翻译文件 [resources/assets/npu/lang](src/main/resources/assets/npu/lang) 中添加翻译

NpuBlocks.class里声明了常用材料属性的枚举，有需求可以自己加

### 注册新物品

1. 根据所需属性在 [resources/register/npu/item/template](src/main/resources/register/npu/item/template) 中寻找一个合适的模板，如果都不合适就自己照猫画虎做一个
2. 在 [resources/register/npu/item/register](src/main/resources/register/npu/item/register) 中找到你希望加入的物品栏文件，在里面相应的模板下加入新物品的id
3. 将对应物品状态写到 [resources/assets/npu/items](src/main/resources/assets/npu/items) 中
4. 在 [resources/assets/npu/models/item](src/main/resources/assets/npu/models/item) 中添加新物品的模型
5. 在 [resources/assets/npu/textures/item](src/main/resources/assets/npu/textures/item) 中添加物品的贴图
6. 根据原模组翻译文件向翻译文件 [resources/assets/npu/lang](src/main/resources/assets/npu/lang) 中添加翻译

### 注册新实体

注意涉及到的不同的字段命名EXAMPLE,EXAMPLE_ID

由于实体的注册变数较大，所以目前还不支持直接从资源文件中通过json文件创建。

从代码中创建细节比较多，但基本操作流程如下

1. 你可以选择继承原版已有的实体，或者继承我已经创建好的实体模板（NpuVehicle 其它模板敬请期待），或是从更基础的类自己创建一个实体（这会很麻烦）
2. 实现你想要的功能
3. 创建一个实体渲染类
4. 创建一个实体模型类
5. 创建一个实体渲染状态类（仅基于原版创建的实体需要）
6. 将实体渲染类注册到 [NPUClient.class](src/client/java/com/tf/npu/client/NPUClient.java) 中（如果你的基类不是Mob或其子类的话则不需要）
7. 将实体注册到[NpuEntities.class](src/main/java/com/tf/npu/entities/NpuEntities.java)中
8. 向翻译文件 [resources/assets/npu/lang](src/main/resources/assets/npu/lang) 中添加翻译

### 注册新创造模式物品栏

1. 根据所需属性在 [resources/register/npu/item_group/template](src/main/resources/register/npu/item_group/template) 中寻找一个合适的模板，如果都不合适就自己照猫画虎做一个
2. 在 [resources/register/npu/item_group/register.json](src/main/resources/register/npu/item_group/register.json) 中相应的模板下加入新物品栏的id
3. 向翻译文件 [resources/assets/npu/lang](src/main/resources/assets/npu/lang) 中添加翻译

## 后记

- 有新的模板或API的需求或者已有的模板或API有更优化的方案可以告诉我

[English](Instructions.en_us.md)

[返回](README.md)