# Desktop

Java desktop functions. Have you tried to find user default Download folder using java? If so, you would find this
library very helpful.

## Example

public class Example {

    public static void main(String[] args) {
        Desktop d = Desktop.desktop();

        System.out.println("Home: " + d.getHome());
        System.out.println("Documents: " + d.getDocuments());
        System.out.println("AppFolder: " + d.getAppData());
        System.out.println("Desktop: " + d.getDesktop());
        System.out.println("Downloads: " + d.getDownloads());
    }
}

## Central Maven Repo

	<dependencies>
		<dependency>
		  <groupId>com.github.axet</groupId>
		  <artifactId>desktop</artifactId>
		  <version>0.0.5</version>
		</dependency>
	</dependencies>
		
