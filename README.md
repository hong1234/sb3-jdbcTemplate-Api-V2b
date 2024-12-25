## Rest API Spring JDBC n-m-relation

## add a Student

POST localhost:8080/api/student

{
    "name": "Test",
    "age": 22,
    "courses": [
        {
            "courseId": "452e527f-0921-4972-aa0f-7e1a7a25263c",
            "description": "English"
        },
        {
            "courseId": "700f0591-7780-4f47-bc6a-274b82f62977",
            "description": "Science"
        }
    ]
}

or 

{
    "name": "Test2",
    "age": 22,
    "courses": []
}

or

{
    "name": "Test3",
    "age": 22
}

## get a Student with ID = 5ca258d9-2e63-47c9-a6ae-0011e72b69df

GET localhost:8080/api/student/5ca258d9-2e63-47c9-a6ae-0011e72b69df

## get course with ID = 452e527f-0921-4972-aa0f-7e1a7a25263c

GET localhost:8080/api/course/452e527f-0921-4972-aa0f-7e1a7a25263c