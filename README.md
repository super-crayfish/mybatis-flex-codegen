# MyBatis-Flex Code Generator

A web-based tool for generating MyBatis-Flex code from SQL CREATE statements.

## Features

- Parse multiple SQL CREATE statements
- Generate MyBatis-Flex entity classes with annotations
- Generate Mapper interfaces
- Generate Service and ServiceImpl classes
- Generate RESTful Controllers
- Customizable package and module names
- Option to use Lombok annotations

## Technologies Used

- Spring Boot 2.7.x
- MyBatis-Flex 1.5.x
- Thymeleaf
- Bootstrap 5
- Prism.js for syntax highlighting

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

4. Open your browser and go to `http://localhost:12000`

## Usage

1. Enter your SQL CREATE statements in the text area
2. Configure the package name, module name, and other options
3. Click "Generate Code"
4. View and copy the generated code

## Sample SQL

A sample SQL file is provided at `/src/main/resources/static/sample.sql` for testing purposes.

## Project Structure

```
src/main/java/com/example/mybatisflexcodegen/
├── controller/
│   ├── CodeGenerationController.java
│   └── IndexController.java
├── model/
│   ├── CodeGenerationRequest.java
│   ├── CodeGenerationResult.java
│   ├── ColumnInfo.java
│   └── TableInfo.java
├── service/
│   └── CodeGenerationService.java
├── util/
│   ├── CodeGenerator.java
│   └── SqlParser.java
└── MybatisFlexCodegenApplication.java
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.