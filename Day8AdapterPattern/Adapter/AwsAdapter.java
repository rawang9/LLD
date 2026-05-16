package Day8AdapterPattern.Adapter;

import Day8AdapterPattern.CloudProvider.AwsStorage;

public class AwsAdapter implements IFileUploaderAdapter {

    private final AwsStorage adaptee;

    public AwsAdapter(AwsStorage adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void upload() {
        adaptee.putObject();
    }
}
