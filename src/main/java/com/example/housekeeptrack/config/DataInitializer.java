package com.example.housekeeptrack.config;

import com.example.housekeeptrack.entity.*;
import com.example.housekeeptrack.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;
    private final HousekeeperRepository housekeeperRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final InspectionRepository inspectionRepository;
    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;
    private final AuditLogRepository auditLogRepository;

    public DataInitializer(RoomRepository roomRepository,
                           HousekeeperRepository housekeeperRepository,
                           CleaningTaskRepository cleaningTaskRepository,
                           InspectionRepository inspectionRepository,
                           GuestRepository guestRepository,
                           BookingRepository bookingRepository,
                           AuditLogRepository auditLogRepository) {
        this.roomRepository = roomRepository;
        this.housekeeperRepository = housekeeperRepository;
        this.cleaningTaskRepository = cleaningTaskRepository;
        this.inspectionRepository = inspectionRepository;
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Seeding initial luxury hotel data for HouseKeepTrack Hotel...");

        // 1. Housekeepers
        Housekeeper anita = new Housekeeper("Anita", "+91 98765 01001", "anita.hk@housekeeptrack.com", HousekeeperStatus.BUSY);
        Housekeeper priya = new Housekeeper("Priya", "+91 98765 01002", "priya.hk@housekeeptrack.com", HousekeeperStatus.BUSY);
        Housekeeper kavya = new Housekeeper("Kavya", "+91 98765 01003", "kavya.hk@housekeeptrack.com", HousekeeperStatus.BUSY);
        Housekeeper meena = new Housekeeper("Meena", "+91 98765 01004", "meena.hk@housekeeptrack.com", HousekeeperStatus.BUSY);
        Housekeeper rahul = new Housekeeper("Rahul", "+91 98765 01005", "rahul.hk@housekeeptrack.com", HousekeeperStatus.AVAILABLE);

        List<Housekeeper> savedHousekeepers = housekeeperRepository.saveAll(Arrays.asList(anita, priya, kavya, meena, rahul));
        anita = savedHousekeepers.get(0);
        priya = savedHousekeepers.get(1);
        kavya = savedHousekeepers.get(2);
        meena = savedHousekeepers.get(3);
        rahul = savedHousekeepers.get(4);

        // 2. Rooms
        // 101 Deluxe READY
        Room r101 = new Room("101", 1, RoomType.DELUXE, RoomStatus.READY, 2, 16000.0);
        r101.setDescription("Opulent room with handcrafted rosewood furniture, marble bath, and panoramic courtyard garden views.");
        r101.setImageUrl("https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80");

        // 102 Deluxe DIRTY
        Room r102 = new Room("102", 1, RoomType.DELUXE, RoomStatus.DIRTY, 2, 16000.0);
        r102.setDescription("Classic luxury accommodation with signature royal bedding, brass accents, and an expansive vanity.");
        r102.setImageUrl("https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80");

        // 103 Premium CLEANING
        Room r103 = new Room("103", 1, RoomType.PREMIUM, RoomStatus.CLEANING, 3, 22000.0);
        r103.setDescription("Spacious premium club suite featuring bespoke artwork, Italian marble floors, and private butler pantry.");
        r103.setImageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1200&q=80");

        // 201 Suite INSPECTED
        Room r201 = new Room("201", 2, RoomType.SUITE, RoomStatus.INSPECTED, 4, 35000.0);
        r201.setDescription("Grand Heritage Suite offering a separate master bedroom, gilded living salon, and private whirlpool spa.");
        r201.setImageUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=1200&q=80");

        // 202 Deluxe READY
        Room r202 = new Room("202", 2, RoomType.DELUXE, RoomStatus.READY, 2, 17500.0);
        r202.setDescription("Serene garden-facing luxury chamber bathed in warm light with bespoke silk draperies and rain shower.");
        r202.setImageUrl("https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=80");

        // 203 Premium DIRTY
        Room r203 = new Room("203", 2, RoomType.PREMIUM, RoomStatus.DIRTY, 3, 24000.0);
        r203.setDescription("High-floor premium room offering elevated city skyline vistas and luxury aromatherapy amenities.");
        r203.setImageUrl("https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1200&q=80");

        // Additional luxury suites
        Room r301 = new Room("301", 3, RoomType.PRESIDENTIAL, RoomStatus.READY, 4, 65000.0);
        r301.setDescription("The Crown Jewel Presidential Suite: custom chandeliers, private dining room, and bulletproof glass balcony.");
        r301.setImageUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=1200&q=80");

        Room r302 = new Room("302", 3, RoomType.SUITE, RoomStatus.READY, 4, 38000.0);
        r302.setDescription("Executive Royal Suite with study library, marble fireplace, and panoramic skyline vistas.");
        r302.setImageUrl("https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=1200&q=80");

        List<Room> savedRooms = roomRepository.saveAll(Arrays.asList(r101, r102, r103, r201, r202, r203, r301, r302));
        r101 = savedRooms.get(0);
        r102 = savedRooms.get(1);
        r103 = savedRooms.get(2);
        r201 = savedRooms.get(3);
        r202 = savedRooms.get(4);
        r203 = savedRooms.get(5);
        r301 = savedRooms.get(6);
        r302 = savedRooms.get(7);

        // 3. Guests
        Guest g1 = new Guest("Vikram Malhotra", "+91 98765 11111", "vikram.m@example.com", "Malabar Hill, Mumbai");
        Guest g2 = new Guest("Ananya Sharma", "+91 98111 22222", "ananya.s@example.com", "Golf Links, New Delhi");
        Guest g3 = new Guest("Rajesh Khanna", "+91 98222 33333", "rajesh.k@example.com", "Alipore, Kolkata");
        Guest g4 = new Guest("Pooja Hegde", "+91 98333 44444", "pooja.h@example.com", "Banjara Hills, Hyderabad");
        List<Guest> savedGuests = guestRepository.saveAll(Arrays.asList(g1, g2, g3, g4));
        g1 = savedGuests.get(0);
        g2 = savedGuests.get(1);
        g3 = savedGuests.get(2);
        g4 = savedGuests.get(3);

        // 4. Cleaning Tasks mapped to initial room states
        // Room 102 (DIRTY) -> Assigned to Anita
        CleaningTask task102 = new CleaningTask(r102, anita, CleaningTaskStatus.ASSIGNED, "Guest checked out. Full sanitation required.");
        task102.setAssignedAt(LocalDateTime.now().minusMinutes(25));
        CleaningTask savedTask102 = cleaningTaskRepository.save(task102);
        anita.setCurrentTaskId(savedTask102.getId());
        housekeeperRepository.save(anita);

        // Room 103 (CLEANING) -> In Progress with Priya
        CleaningTask task103 = new CleaningTask(r103, priya, CleaningTaskStatus.IN_PROGRESS, "Linen refresh and carpet vacuuming in progress.");
        task103.setAssignedAt(LocalDateTime.now().minusMinutes(40));
        task103.setStartedAt(LocalDateTime.now().minusMinutes(20));
        CleaningTask savedTask103 = cleaningTaskRepository.save(task103);
        priya.setCurrentTaskId(savedTask103.getId());
        housekeeperRepository.save(priya);

        // Room 201 (INSPECTED) -> Completed by Kavya, awaiting supervisor pass/fail
        CleaningTask task201 = new CleaningTask(r201, kavya, CleaningTaskStatus.COMPLETED, "Cleaning complete. Ready for supervisor inspection.");
        task201.setAssignedAt(LocalDateTime.now().minusHours(1).minusMinutes(15));
        task201.setStartedAt(LocalDateTime.now().minusHours(1));
        task201.setCompletedAt(LocalDateTime.now().minusMinutes(15));
        CleaningTask savedTask201 = cleaningTaskRepository.save(task201);
        kavya.setCurrentTaskId(savedTask201.getId());
        housekeeperRepository.save(kavya);

        // Room 203 (DIRTY) -> Assigned to Meena
        CleaningTask task203 = new CleaningTask(r203, meena, CleaningTaskStatus.ASSIGNED, "Standard cleaning after corporate event checkout.");
        task203.setAssignedAt(LocalDateTime.now().minusMinutes(10));
        CleaningTask savedTask203 = cleaningTaskRepository.save(task203);
        meena.setCurrentTaskId(savedTask203.getId());
        housekeeperRepository.save(meena);

        // 5. Inspections
        Inspection pastInspection = new Inspection(r101, "Supervisor David", InspectionStatus.PASSED, "White glove check passed with high marks.");
        pastInspection.setInspectedAt(LocalDateTime.now().minusHours(2));
        inspectionRepository.save(pastInspection);

        // 6. Bookings
        // An active checked-in booking for Room 202 (READY room)
        Booking b1 = new Booking(g2, r202, LocalDate.now(), LocalDate.now().plusDays(2), BookingStatus.CHECKED_IN);
        b1.setTotalAmount(35000.0);
        bookingRepository.save(b1);

        // An active checked-in booking for Room 301 (READY room)
        Booking b2 = new Booking(g3, r301, LocalDate.now(), LocalDate.now().plusDays(3), BookingStatus.CHECKED_IN);
        b2.setTotalAmount(195000.0);
        bookingRepository.save(b2);

        // A historical checked-out booking for Room 102
        Booking b3 = new Booking(g1, r102, LocalDate.now().minusDays(2), LocalDate.now(), BookingStatus.CHECKED_OUT);
        b3.setTotalAmount(32000.0);
        bookingRepository.save(b3);

        // 7. Audit logs
        auditLogRepository.save(new AuditLog("SYSTEM", 1L, "SYSTEM_INIT", null, "HouseKeepTrack Initialized", "SYSTEM"));
        auditLogRepository.save(new AuditLog("BOOKING", b3.getId(), "BOOKING_CHECKED_OUT", "CHECKED_IN", "CHECKED_OUT", "RECEPTIONIST"));
        auditLogRepository.save(new AuditLog("ROOM", r102.getId(), "ROOM_STATUS_CHANGED", "READY", "DIRTY", "RECEPTIONIST"));
        auditLogRepository.save(new AuditLog("TASK", savedTask102.getId(), "TASK_ASSIGNED", "NONE", anita.getName(), "AUTO_ASSIGNER"));

        log.info("Seeding completed successfully with 8 rooms, 5 housekeepers, active tasks, and initial bookings.");
    }
}
