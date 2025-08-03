# BuildTheWorldForNPU

Made by Infinomat

## Convention

- When registering new things and storing new resource files, keep things organized. Write code where it should be written and put things where they should be placed.
- Add logs whenever possible.
- Keep the code beautiful.

## Refactored Structure Overview

- npu
    - client Client entry point
    - renders Renderers
        - entity Entity renderers
    - blocks
        - npublocknewclasses Templates for new blocks
        - NpuBlocks.class All new blocks will be automatically registered here
    - entities
        - npuentitynewclasses Templates for new entities
        - NpuEntities.class All new entities should be registered here
        - NpuEntitySubscriber.class Links new entities and new rendering methods to events
    - itemgroups
        - CreativeModeTab.class Used to add new items to the vanilla creative mode tab
        - NpuCreativeModeTabs.class All new creative mode tabs should be registered here
    - items
        - npuitemnewclasses Templates for new items
        - NpuItems.class All new items (including block items) will be automatically registered here
        - util
            - register
                - data
                    - template Data for new objects
                    - RegisterList.class Used to get the ID and template of new objects
                - RegisterObjects.class Creates files and writes data
            - FileDataGetter.class Gets JSON data from files
            - FolderDataGetter.class Gets JSON data from folders
            - Reference.class Used to get mod basic information
            - PathTools.class Used to process paths
        - NPU Main class (generally don't touch it)

## How to Operate

### Registering New Blocks

1. Determine the block type

   NORMAL_STRUCTURE Normal block

   HORIZONTAL_DIRECTIONAL_STRUCTURE Block with four directions (North, South, East, West)

   HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE Block with twelve directions

   NORMAL_HALF_SLAB Normal stackable slab block

   HORIZONTAL_DIRECTIONAL_HALF_SLAB Stackable slab block with four directions (North, South, East, West)

   DOOR_AND_WINDOW Block with two models (open and closed)

   (More types coming soon)
2. Determine the block material

   IRON

   ROCK

   (More types coming soon)
3. Find a suitable template based on the required properties in [resources/register/npu/block/template](src/main/resources/register/npu/block/template). If none are suitable, create one by following existing examples.
4. Find the item group file you want to add to in [resources/register/npu/block/register](src/main/resources/register/npu/block/register) and add the new block's ID under the appropriate template.
5. Write the block state to [resources/assets/npu/blockstates](src/main/resources/assets/npu/blockstates).
6. Write the corresponding item state to [resources/assets/npu/items](src/main/resources/assets/npu/items).
7. Add the new block's model in [resources/assets/npu/models/block](src/main/resources/assets/npu/models/block).
8. Add the block's texture in [resources/assets/npu/textures/block](src/main/resources/assets/npu/textures/block).
9. Add translations to the translation file [resources/assets/npu/lang](src/main/resources/assets/npu/lang) according to the original mod translation files.

[NpuBlocks.class](file://E:\Documents\Java\McMod\BuildTheWordForNPU\build\classes\java\main\com\tf\npu\blocks\NpuBlocks.class) declares enums for common material properties. You can add your own if needed.

### Registering New Items

1. Find a suitable template based on the required properties in [resources/register/npu/item/template](src/main/resources/register/npu/item/template). If none are suitable, create one by following existing examples.
2. Find the item group file you want to add to in [resources/register/npu/item/register](src/main/resources/register/npu/item/register) and add the new item's ID under the appropriate template.
3. Write the corresponding item state to [resources/assets/npu/items](src/main/resources/assets/npu/items).
4. Add the new item's model in [resources/assets/npu/models/item](src/main/resources/assets/npu/models/item).
5. Add the item's texture in [resources/assets/npu/textures/item](src/main/resources/assets/npu/textures/item).
6. Add translations to the translation file [resources/assets/npu/lang](src/main/resources/assets/npu/lang) according to the original mod translation files.

### Registering New Entities

Note the different field naming conventions like EXAMPLE, EXAMPLE_ID.

Since entity registration has many variables, direct creation from JSON files in resource files is not currently supported.

Creating from code involves many details, but the basic operation process is as follows:

1. You can choose to inherit from existing vanilla entities, or inherit from templates I've already created (NpuVehicle, more templates coming soon), or create an entity from a more basic class (this will be complicated).
2. Implement the functionality you want.
3. Create an entity renderer class.
4. Create an entity model class.
5. Create an entity render state class (only needed for entities based on vanilla entities).
6. Register the entity renderer class to [NPUClient.class](src/client/java/com/tf/npu/client/NPUClient.java) (not needed if your base class is not Mob or its subclass).
7. Register the entity to [NpuEntities.class](src/main/java/com/tf/npu/entities/NpuEntities.java).
8. Add translations to the translation file [resources/assets/npu/lang](src/main/resources/assets/npu/lang).

### Registering New Creative Mode Tabs

1. Find a suitable template based on the required properties in [resources/register/npu/item_group/template](src/main/resources/register/npu/item_group/template). If none are suitable, create one by following existing examples.
2. Add the new tab's ID under the appropriate template in [resources/register/npu/item_group/register.json](src/main/resources/register/npu/item_group/register.json).
3. Add translations to the translation file [resources/assets/npu/lang](src/main/resources/assets/npu/lang).

## Postscript

- If you have new template or API requirements, or if there are more optimized solutions for existing templates or APIs, please let me know.

[中文](Instructions.zh_cn.md)

[Back](README.md)
