package Day8AdapterPattern.Adapter;
import Day8AdapterPattern.CloudProvider.GoogleStorage;


public class GoogleAdapter implements IFileUploaderAdapter {

    private final GoogleStorage adaptee;

    public GoogleAdapter(GoogleStorage adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void upload() {
        adaptee.uploadBlob();
    }
}
