# DevHelp

A Java Swing application providing a collection of useful tools for text manipulation, web requests, and number conversions (Many bug to fix, pull and customize as you want).

## Features

* **Compare Text:** Easily compare two pieces of text to identify differences.
* **Format Text:** Apply various formatting options to text, such as changing case or adding prefixes/suffixes.
* **HTTP Request Tool:** Send HTTP requests (GET, POST, etc.) and view responses, similar to Postman.
* **Number to Text:** Convert numerical values into their textual representation.

## How to Use

1.  **Prerequisites:** Make sure you have Java Runtime Environment (JRE) installed on your system.
2.  **Download:**
    ```bash
    git clone https://github.com/phean-dev/DevHelp.git
    ```
3.  **Build (if necessary):** If you downloaded the source code, navigate to the project directory and build it using your preferred Java build tool (e.g., Maven, Gradle).
    ```bash
    # Example using gradle
    gradle clean build
    ```
4.  **Run:** Execute the generated `.jar` file located in the `target` or `build/libs` directory.
    ```bash
    java -jar DevHelp.jar
    ```

### Using the Tools

* **Compare Text:** Enter the two texts you want to compare in the respective fields and click Ctrl + Enter. The differences will be highlighted or displayed.
* **Format Text:** Input your text, select the desired formatting options, and Ctrl + F. The formatted text will be shown.
* **HTTP Request Tool:** Enter the URL, select the HTTP method, add any necessary headers or body, and click "Send." The response will be displayed.
* **Number to Text:** Enter a number on one side and input number. The textual representation shown in other side.

## Technologies Used

* Java Swing

## Contributing

Feel free to contribute by reporting issues or submitting pull requests.