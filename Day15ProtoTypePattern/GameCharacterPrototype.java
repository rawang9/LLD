package Day15ProtoTypePattern;

/**
 * Prototype: declare cloning so clients copy existing objects instead of rebuilding.
 */
public interface GameCharacterPrototype {

    GameCharacter cloneCharacter();
}
