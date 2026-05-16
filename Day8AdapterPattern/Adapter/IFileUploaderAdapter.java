package Day8AdapterPattern.Adapter;

/**
 * Target: the interface your app expects. Every cloud backend is exposed through this.
 */
public interface IFileUploaderAdapter {

    void upload();
}
