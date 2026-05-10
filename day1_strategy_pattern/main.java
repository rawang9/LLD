import ducks.CloudDuck;
import ducks.Duck;
import ducks.MountainDuck;
import strategy.MountainFly;

class Main {
    public static void main(String[] args) {
        Duck cloud = new CloudDuck();
        Duck mountain = new MountainDuck();

        cloud.display();
        cloud.quack();
        cloud.fly();

        mountain.display();
        mountain.quack();
        mountain.fly();

        cloud.setFlyBehavior(new MountainFly());
        cloud.fly();
    }
}
