package Day8AdapterPattern;

import Day8AdapterPattern.Adapter.AwsAdapter;
import Day8AdapterPattern.Adapter.GoogleAdapter;
import Day8AdapterPattern.Adapter.IFileUploaderAdapter;
import Day8AdapterPattern.CloudProvider.AwsStorage;
import Day8AdapterPattern.CloudProvider.GoogleStorage;

class Main {

    public static void main(String[] args) {
        System.out.println("Learning Adapter pattern");

        // Client depends on Target (interface), not on AWS/Google APIs
        IFileUploaderAdapter awsUploader = new AwsAdapter(new AwsStorage());
        IFileUploaderAdapter googleUploader = new GoogleAdapter(new GoogleStorage());

        upload(awsUploader, "AWS");
        upload(googleUploader, "Google");
    }

    private static void upload(IFileUploaderAdapter uploader, String provider) {
        System.out.println("Uploading via " + provider);
        uploader.upload();
    }
}
