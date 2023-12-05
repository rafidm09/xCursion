DROP DATABASE IF EXISTS xCursion;
CREATE DATABASE xCursion; 
USE xCursion;


-- Create Plane Table
CREATE TABLE Plane (
    PlaneID 				INT PRIMARY KEY,
    PlaneType 			VARCHAR(50) NOT NULL,
    CrewSize 				INT NOT NULL,
    AvailabilityStatus 		VARCHAR(50) NOT NULL
);

INSERT INTO Plane (PlaneID, PlaneType, CrewSize, AvailabilityStatus)
VALUES
    (1, 'Boeing 747', 5, 'Active'),
    (2, 'Airbus A380', 8, 'Active'),
    (3, 'Boeing 787 Dreamliner', 7, 'Active'),
    (4, 'Airbus A330', 6, 'Active'),
    (5, 'Boeing 747', 5, 'Inactive');


-- Create FlightCrew Table
CREATE TABLE FlightCrew ( 	
    FlightCrewID            INT PRIMARY KEY
);

-- Insert data into FlightCrew table
INSERT INTO FlightCrew (FlightCrewID)
VALUES
    (1),
    (2),
    (3);

DROP TABLE IF EXISTS Flight;
CREATE TABLE Flight(
    FlightID			    varchar(45) not null,
	DepartTime				varchar(45) not null,
    ArrivalTime				varchar(45) not null,
	Origin					varchar(45) not null,
	Destination				varchar(45) not null,
    CostRegular				int not null,
    CostBusiness			int not null,
    PlaneID					int not null,
    FlightCrewID            int,
	PRIMARY KEY (FlightID),
    FOREIGN KEY (PlaneID) REFERENCES Plane(PlaneID),
    FOREIGN KEY (FlightCrewID) REFERENCES FlightCrew(FlightCrewID)
);


INSERT INTO Flight (FlightID, DepartTime, ArrivalTime, Origin, Destination, CostRegular, CostBusiness, PlaneID, FlightCrewID)
VALUES
('A98', '2023-12-01T15:45:00', '2023-12-01T18:00:00', 'ANC', 'SEA', 150, 275, 2, 1),
('AA2336', '2023-12-05T09:20:00', '2023-12-05T11:35:00', 'LAX', 'PBI', 200, 350, 4, 2),
('US840', '2023-12-10T18:05:00', '2023-12-10T20:20:00', 'SFO', 'CLT', 100, 175, 3, 1),
('AA258', '2023-12-15T04:30:00', '2023-12-15T06:45:00', 'LAX', 'MIA', 450, 650, 1, 3),
('AS135', '2023-12-20T12:55:00', '2023-12-20T15:10:00', 'SEA', 'ANC', 400, 600, 2, 2),
('DL806', '2023-12-25T21:40:00', '2023-12-26T00:00:00', 'SFO', 'MSP', 350, 425, 4, 3),
('NK612', '2023-12-29T08:10:00', '2023-12-29T10:25:00', 'LAS', 'MSP', 275, 375, 3, 1),
('US2013', '2024-01-03T16:35:00', '2024-01-03T18:50:00', 'LAX', 'CLT', 200, 325, 2, 2),
('AA1112', '2024-01-08T01:00:00', '2024-01-08T03:15:00', 'SFO', 'DFW', 175, 250, 1, 1),
('DL1173', '2024-01-13T09:25:00', '2024-01-13T11:40:00', 'LAS', 'ATL', 675, 800, 4, 3),
('DL2336', '2024-01-18T18:10:00', '2024-01-18T20:25:00', 'DEN', 'ATL', 500, 725, 3, 2);

CREATE TABLE Users (
    UserID              INT PRIMARY KEY AUTO_INCREMENT,
    UserName            VARCHAR(50),
    Address             VARCHAR(100),
    Email               VARCHAR(100),
    UserType            VARCHAR(20) NOT NULL,
    pwd 				VARCHAR(20) 
);

INSERT INTO Users(UserID, UserName, Address, Email, UserType, pwd)
VALUES
    (1, 'John Doe', '123 Main St', 'john.doe@example.com', 'Registered' , 'password1'),
    (2, 'Jane Smith', '456 Oak St', 'jane.smith@example.com', 'Guest', null),
    (3, 'Bob Johnson', '789 Pine St', 'bob.johnson@example.com', 'FlightAttendant', 'password2'),
    (4, 'Alice Brown', '101 Elm St', 'alice.brown@example.com', 'Admin', 'password3'),
    (5, 'Charlie White', '202 Maple St', 'charlie.white@example.com', 'Registered', 'password4'),
    (6, 'Eva Black', '303 Cedar St', 'eva.black@example.com', 'AirlineAgent', 'password5'),
    (7, 'David Gray', '404 Birch St', 'david.gray@example.com', 'Guest', null),
    (8, 'Fiona Green', '505 Walnut St', 'fiona.green@example.com', 'Registered', 'password6'),
    (9, 'George Red', '606 Sycamore St', 'george.red@example.com', 'FlightAttendant', 'password7'),
    (10, 'Helen Orange', '707 Cedar St', 'helen.orange@example.com', 'Admin', 'password8'),
    (11, 'Ivan Yellow', '808 Pine St', 'ivan.yellow@example.com', 'AirlineAgent', 'password9'),
    (12, 'Jessica Purple', '909 Elm St', 'jessica.purple@example.com', 'Registered', 'password10'),
    (13, 'Kevin Brown', '111 Oak St', 'kevin.brown@example.com', 'Guest', null),
    (14, 'Laura Gray', '222 Maple St', 'laura.gray@example.com', 'FlightAttendant', 'password11'),
    (15, 'Mike Black', '333 Pine St', 'mike.black@example.com', 'Admin', 'password12'),
    (16, 'Nina Green', '444 Cedar St', 'nina.green@example.com', 'Registered', 'password13'),
    (17, 'Oscar Red', '555 Birch St', 'oscar.red@example.com', 'AirlineAgent', 'password14'),
    (18, 'Pam Orange', '666 Walnut St', 'pam.orange@example.com', 'Guest', null),
    (19, 'Quincy Yellow', '777 Sycamore St', 'quincy.yellow@example.com', 'FlightAttendant', 'password15'),
    (20, 'Rachel Purple', '888 Cedar St', 'rachel.purple@example.com', 'Admin', 'password16'),
    (21, 'Sam Brown', '999 Elm St', 'sam.brown@example.com', 'AirlineAgent', 'password17'),
    (22, 'Tina Gray', '121 Oak St', 'tina.gray@example.com', 'Registered', 'password18'),
    (23, 'Ulysses Black', '232 Maple St', 'ulysses.black@example.com', 'Guest', null),
    (24, 'Vera Green', '343 Pine St', 'vera.green@example.com', 'FlightAttendant', 'password19'),
    (25, 'Walter Red', '454 Cedar St', 'walter.red@example.com', 'Admin', 'password20'),
    (26, 'Xena Orange', '565 Birch St', 'xena.orange@example.com', 'AirlineAgent', 'password21'),
    (27, 'Yuri Yellow', '676 Walnut St', 'yuri.yellow@example.com', 'Guest', null),
    (28, 'Zoe Purple', '787 Sycamore St', 'zoe.purple@example.com', 'FlightAttendant', 'password22'),
    (29, 'Adam Brown', '898 Cedar St', 'adam.brown@example.com', 'Admin', 'password23'),
    (30, 'Eve Gray', '909 Elm St', 'eve.gray@example.com', 'AirlineAgent', 'password24'),
    (31, 'Alice Green', '123 Oak Lane', 'alice.green@example.com', 'FlightAttendant', 'password25'),
    (32, 'Bob Smith', '456 Maple Ave', 'bob.smith@example.com', 'FlightAttendant', 'password26'),
    (33, 'Catherine Brown', '789 Elm Street', 'catherine.brown@example.com', 'FlightAttendant', 'password27'),
    (34, 'David White', '101 Pine Road', 'david.white@example.com', 'FlightAttendant', 'password28'),
    (35, 'Eva Johnson', '222 Birch Boulevard', 'eva.johnson@example.com', 'FlightAttendant', 'password29'),
    (36, 'Frank Miller', '333 Cedar Drive', 'frank.miller@example.com', 'FlightAttendant', 'password30');
    
    
    
-- Create FlightAttendant Table
CREATE TABLE FlightAttendant (
    FlightAttendantID           INT PRIMARY KEY AUTO_INCREMENT,
    UserID                      INT UNIQUE,
    FlightCrewID                INT,
    FOREIGN KEY (UserID)        REFERENCES Users(UserID) ON DELETE CASCADE,
    FOREIGN KEY (FlightCrewID)  REFERENCES FlightCrew(FlightCrewID)
);

-- Insert data into FlightAttendant table
INSERT INTO FlightAttendant (FlightAttendantID, UserID, FlightCrewID)
VALUES
    (1, 3, 1),  
    (2, 9, 1),   
    (3, 14, 1),  
    (4, 19, 1),
    (5, 24, 2),  
    (6, 28, 2),
    (7, 31, 2),  
    (8, 32, 2),   
    (9, 33, 3),  
    (10, 34, 3),  
    (11, 35, 3),  
    (12, 36, 3); 


-- Create Seat Table
CREATE TABLE Seat (
    SeatID 				INT PRIMARY KEY,
    PlaneID 			INT NOT NULL,
    RowNumber			INT NOT NULL,
    SeatPosition		VARCHAR(5) NOT NULL,		
    SeatType 			VARCHAR(20) NOT NULL, -- Regular or Business
    Available			TINYINT NOT NULL,
    FOREIGN KEY (PlaneID) REFERENCES Plane(PlaneID)
);

-- Insert data into Seat table
INSERT INTO Seat (SeatID, PlaneID, RowNumber, SeatPosition, SeatType, Available)
VALUES
	(1, 1, 1, 'A', 'Business', 1),
    (2, 1, 1, 'B', 'Business', 1),
    (3, 1, 1, 'C', 'Business', 1),
    (4, 1, 1, 'D', 'Business', 1),
    (5, 1, 1, 'E', 'Business', 1),
    (6, 1, 1, 'F', 'Business', 1),
    (7, 1, 2, 'A', 'Regular', 1),
    (8, 1, 2, 'B', 'Regular', 1),
    (9, 1, 2, 'C', 'Regular', 1),
    (10, 1, 2, 'D', 'Regular', 1),
    (11, 1, 2, 'E', 'Regular', 1),
    (12, 1, 2, 'F', 'Regular', 1),
    (13, 1, 3, 'A', 'Regular', 1),
    (14, 1, 3, 'B', 'Regular', 1),
    (15, 1, 3, 'C', 'Regular', 1),
    (16, 1, 3, 'D', 'Regular', 1),
    (17, 1, 3, 'E', 'Regular', 1),
    (18, 1, 3, 'F', 'Regular', 1),
    (19, 1, 4, 'A', 'Regular', 1),
    (20, 1, 4, 'B', 'Regular', 1),
    (21, 1, 4, 'C', 'Regular', 1),
    (22, 1, 4, 'D', 'Regular', 1),
    (23, 1, 4, 'E', 'Regular', 1),
    (24, 1, 4, 'F', 'Regular', 1),
    
    (25, 2, 1, 'A', 'Business', 1),
    (26, 2, 1, 'B', 'Business', 1),
    (27, 2, 1, 'C', 'Business', 1),
    (28, 2, 1, 'D', 'Business', 1),
    (29, 2, 1, 'E', 'Business', 1),
    (30, 2, 1, 'F', 'Business', 1),
    (31, 2, 2, 'A', 'Regular', 1),
    (32, 2, 2, 'B', 'Regular', 1),
    (33, 2, 2, 'C', 'Regular', 1),
    (34, 2, 2, 'D', 'Regular', 1),
    (35, 2, 2, 'E', 'Regular', 1),
    (36, 2, 2, 'F', 'Regular', 1),
    (37, 2, 3, 'A', 'Regular', 1),
    (38, 2, 3, 'B', 'Regular', 1),
    (39, 2, 3, 'C', 'Regular', 1),
    (40, 2, 3, 'D', 'Regular', 1),
    (41, 2, 3, 'E', 'Regular', 1),
    (42, 2, 3, 'F', 'Regular', 1),
    (43, 2, 4, 'A', 'Regular', 1),
    (44, 2, 4, 'B', 'Regular', 1),
    (45, 2, 4, 'C', 'Regular', 1),
    (46, 2, 4, 'D', 'Regular', 1),
    (47, 2, 4, 'E', 'Regular', 1),
    (48, 2, 4, 'F', 'Regular', 1),

    (49, 3, 1, 'A', 'Business', 1),
    (50, 3, 1, 'B', 'Business', 1),
    (51, 3, 1, 'C', 'Business', 1),
    (52, 3, 1, 'D', 'Business', 1),
    (53, 3, 1, 'E', 'Business', 1),
    (54, 3, 1, 'F', 'Business', 1),
    (55, 3, 2, 'A', 'Regular', 1),
    (56, 3, 2, 'B', 'Regular', 1),
    (57, 3, 2, 'C', 'Regular', 1),
    (58, 3, 2, 'D', 'Regular', 1),
    (59, 3, 2, 'E', 'Regular', 1),
    (60, 3, 2, 'F', 'Regular', 1),
    (61, 3, 3, 'A', 'Regular', 1),
    (62, 3, 3, 'B', 'Regular', 1),
    (63, 3, 3, 'C', 'Regular', 1),
    (64, 3, 3, 'D', 'Regular', 1),
    (65, 3, 3, 'E', 'Regular', 1),
    (66, 3, 3, 'F', 'Regular', 1),
    (67, 3, 4, 'A', 'Regular', 1),
    (68, 3, 4, 'B', 'Regular', 1),
    (69, 3, 4, 'C', 'Regular', 1),
    (70, 3, 4, 'D', 'Regular', 1),
    (71, 3, 4, 'E', 'Regular', 1),
    (72, 3, 4, 'F', 'Regular', 1),

    (73, 4, 1, 'A', 'Business', 1),
    (74, 4, 1, 'B', 'Business', 1),
    (75, 4, 1, 'C', 'Business', 1),
    (76, 4, 1, 'D', 'Business', 1),
    (77, 4, 1, 'E', 'Business', 1),
    (78, 4, 1, 'F', 'Business', 1),
    (79, 4, 2, 'A', 'Regular', 1),
    (80, 4, 2, 'B', 'Regular', 1),
    (81, 4, 2, 'C', 'Regular', 1),
    (82, 4, 2, 'D', 'Regular', 1),
    (83, 4, 2, 'E', 'Regular', 1),
    (84, 4, 2, 'F', 'Regular', 1),
    (85, 4, 3, 'A', 'Regular', 1),
    (86, 4, 3, 'B', 'Regular', 1),
    (87, 4, 3, 'C', 'Regular', 1),
    (88, 4, 3, 'D', 'Regular', 1),
    (89, 4, 3, 'E', 'Regular', 1),
    (90, 4, 3, 'F', 'Regular', 1),
    (91, 4, 4, 'A', 'Regular', 1),
    (92, 4, 4, 'B', 'Regular', 1),
    (93, 4, 4, 'C', 'Regular', 1),
    (94, 4, 4, 'D', 'Regular', 1),
    (95, 4, 4, 'E', 'Regular', 1),
    (96, 4, 4, 'F', 'Regular', 1),

    (97, 5, 1, 'A', 'Business', 1),
    (98, 5, 1, 'B', 'Business', 1),
    (99, 5, 1, 'C', 'Business', 1),
    (100, 5, 1, 'D', 'Business', 1),
    (101, 5, 1, 'E', 'Business', 1),
    (102, 5, 1, 'F', 'Business', 1),
    (103, 5, 2, 'A', 'Regular', 1),
    (104, 5, 2, 'B', 'Regular', 1),
    (105, 5, 2, 'C', 'Regular', 1),
    (106, 5, 2, 'D', 'Regular', 1),
    (107, 5, 2, 'E', 'Regular', 1),
    (108, 5, 2, 'F', 'Regular', 1),
    (109, 5, 3, 'A', 'Regular', 1),
    (110, 5, 3, 'B', 'Regular', 1),
    (111, 5, 3, 'C', 'Regular', 1),
    (112, 5, 3, 'D', 'Regular', 1),
    (113, 5, 3, 'E', 'Regular', 1),
    (114, 5, 3, 'F', 'Regular', 1),
    (115, 5, 4, 'A', 'Regular', 1),
    (116, 5, 4, 'B', 'Regular', 1),
    (117, 5, 4, 'C', 'Regular', 1),
    (118, 5, 4, 'D', 'Regular', 1),
    (119, 5, 4, 'E', 'Regular', 1),
    (120, 5, 4, 'F', 'Regular', 1);

    


-- Create Reservation Table
CREATE TABLE Ticket (
    TicketID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT,
    FlightID varchar(45),
    SeatID INT,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (FlightID) REFERENCES Flight(FlightID) ON DELETE CASCADE,
    FOREIGN KEY (SeatID) REFERENCES Seat(SeatID)
);

INSERT INTO Ticket (TicketID, UserID, FlightID, SeatID)
VALUES
    (1, 1, 'A98', 25),   
    (2, 2, 'AA2336', 90),  
    (3, 5, 'US840', 70),   
    (4, 8, 'AA258', 1), 
    (5, 12, 'DL806', 88),  
    (6, 18, 'DL806', 89), 
    (7, 22, 'NK612', 69), 
    (8, 23, 'US2013', 30), 
    (9, 13, 'AA1112', 4),  
    (10, 7, 'DL1173', 95), 
    (11, 16, 'AA1112', 7),
    (12, 27, 'AA1112', 8);



-- Create GuestUser Table
CREATE TABLE GuestUser (
    GuestUserID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT UNIQUE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

INSERT INTO GuestUser (UserID)
SELECT u.UserID FROM Users u WHERE u.UserType = 'Guest';



-- Create RegisteredUser Table
CREATE TABLE RegisteredUser (
    RegisteredUserID INT PRIMARY KEY,
    UserID INT UNIQUE,
    CreditCardNumber VARCHAR(16),
    pwdRU varchar(45) not null,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);


-- Insert data into RegisteredUser table
INSERT INTO RegisteredUser (RegisteredUserID, UserID, CreditCardNumber, pwdRU)
VALUES
    (1, 1, '1234567890123456', 'password1'),   
    (2, 5, '2345678901234567', 'password2'),   
    (3, 8, '3456789012345678', 'password3'),   
    (4, 12, '4567890123456789', 'password4'),  
    (5, 16, '5678901234567890', 'password5'),  
    (6, 22, '6789012345678901', 'password6');  
  



-- Create Admin Table
CREATE TABLE Admin (
    AdminID INT PRIMARY KEY,
    UserID INT UNIQUE,
    pwdAdmin varchar(45) not null,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);


-- Insert data into Admin table
INSERT INTO Admin (AdminID, UserID, pwdAdmin)
VALUES
    (1, 4, 'adminPassword1'),    
    (2, 10, 'adminPassword2'),   
    (3, 15, 'adminPassword3'),   
    (4, 20, 'adminPassword4'),   
    (5, 25, 'adminPassword5'),   
    (6, 29, 'adminPassword6');   


-- Create AirlineAgent Table
CREATE TABLE AirlineAgent (
    AirlineAgentID INT PRIMARY KEY,
    UserID INT UNIQUE,
    pwdAgent varchar(45) not null,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

INSERT INTO AirlineAgent (AirlineAgentID, UserID, pwdAgent)
VALUES
    (1, 6, 'passwordAA1'),   
    (2, 11, 'passwordAA2'),  
    (3, 17, 'passwordAA3'),  
    (4, 21, 'passwordAA4'),  
    (5, 26, 'passwordAA5'),  
    (6, 30, 'passwordAA6');  

