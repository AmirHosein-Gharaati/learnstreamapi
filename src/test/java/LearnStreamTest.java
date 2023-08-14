import org.example.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LearnStreamTest {

    private static List<User> users;

    @BeforeAll
    public static void init() {
        users = List.of(
                new User(1L, "Amirhosein", "Gharaati", "amirgh1380@gmail.com", 22, List.of("computer", "board games")),
                new User(2L, "Mohammad", "Shoja", "rezajsh@yahoo.com", 26, List.of("computer", "guitar")),
                new User(3L, "Babak", "Ahmadi", "babakahmadi@gmail.com", 33, List.of("shopping")),
                new User(2L, "Robin", "Eklund", "robin.eklund@twitter.com", 28, List.of("reading")),
                new User(5L, "Amir", "Tavakoli", "amirtvkli@gmail.com", 30, List.of("reading", "computer", "cooking")),
                new User(5L, "Farhad", "Kiani", "farhadkiani@focalpay.se", 28, List.of())
        );
    }

    @Test
    void group_users_by_email() {
        Map<String, Long> emailToCount = users.stream()
                .collect(
                        Collectors.groupingBy(
                                user -> getEmailProvider(user), Collectors.counting()
                        )
                );

        Long numberOfUsersWithGmailAccount = 3L;

        assertEquals(numberOfUsersWithGmailAccount, emailToCount.get("gmail.com"));
    }

    private String getEmailProvider(User user) {
        return user.getEmail().split("@")[1];
    }

    @Test
    void count_computer_interest() {
        long numberOfComputerInterest = users.stream()
                .map(User::getInterests)
                .flatMap(Collection::stream)
                .filter(interest -> interest.equals("computer"))
                .count();
        int expectedCount = 3;

        assertEquals(expectedCount, numberOfComputerInterest);
    }

    @Test
    void extract_all_interests() {
        Set<String> interests = users.stream()
                .map(User::getInterests)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        int numberOfDistinctInterests = 6;

        assertEquals(numberOfDistinctInterests, interests.size());

    }

    @Test
    void extract_duplicated_users_based_on_user_id() {
        Set<Long> duplicatedUsersIds = users.stream()
                .collect(Collectors.groupingBy(User::getId, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Long> expectedIds = Set.of(2L, 5L);

        assertEquals(expectedIds, duplicatedUsersIds);
    }

    @Test
    void traditional_extract_duplicated_users_based_on_user_id() {
        Set<Long> duplicatedUsersIds = new HashSet<>();
        Map<Long, Long> idToCount = generateIdToCount();

        for (Map.Entry<Long, Long> entry : idToCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicatedUsersIds.add(entry.getKey());
            }
        }

        Set<Long> expectedIds = Set.of(2L, 5L);

        assertEquals(expectedIds, duplicatedUsersIds);
    }

    private Map<Long, Long> generateIdToCount() {
        Map<Long, Long> idToCount = new HashMap<>();

        for (User user : users) {
            if (idToCount.containsKey(user.getId())) {
                Long amount = idToCount.get(user.getId());
                idToCount.put(user.getId(), amount + 1);
            } else {
                idToCount.put(user.getId(), 1L);
            }
        }

        return idToCount;
    }

    @Test
    void collect_unique_ids() {
        Set<Long> uniqueIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> expectedUniqueIds = Set.of(1L, 2L, 3L, 5L);

        assertEquals(expectedUniqueIds, uniqueIds);
    }

    @Test
    void count_number_of_users_with_yahoo_email() {
        long numberOfYahooUsers = users.stream()
                .filter(user -> user.getEmail().endsWith("yahoo.com"))
                .count();

        assertEquals(1, numberOfYahooUsers);
    }


    @Test
    void filter_users_with_gmail_with_age_greater_than_equal_25() {
        List<User> filteredUsers = users.stream()
                .filter(user -> hasGmailAccount(user))
                .filter(user -> ageIsGreaterThanEqual25(user))
                .toList();
        int expectedNumOfUsers = 2;

        assertEquals(expectedNumOfUsers, filteredUsers.size());
    }

    private boolean hasGmailAccount(User user) {
        return user.getEmail().endsWith("gmail.com");
    }

    @Test
    void first_person_with_age_greater_than_equal_25() {
        Optional<User> user = users.stream()
                .filter(this::ageIsGreaterThanEqual25)
                .findFirst();

        Integer expectedAge = 26;
        String expectedFirstName = "Mohammad";

        assertEquals(expectedAge, user.get().getAge());
        assertEquals(expectedFirstName, user.get().getFirstName());
    }

    private boolean ageIsGreaterThanEqual25(User user) {
        return user.getAge() >= 25;
    }


    @Test
    public void trim_all_users_emails() {
        users.stream().forEach(user -> {
            user.setEmail(user.getEmail().trim());
        });

        users.forEach(user -> {
            assertFalse(containsWhitespace(user.getEmail()));
        });
    }

    private boolean containsWhitespace(String email) {
        return email.matches("^\\s|\\s$");
    }

    @Test
    void generate_users_fullname() {
        List<String> fullNames = users.stream()
                .map(this::createFullName)
                .toList();

        String expectedFirstPersonFullName = "Amirhosein Gharaati";
        assertEquals(expectedFirstPersonFullName, fullNames.get(0));
    }

    private String createFullName(User user) {
        return "%s %s".formatted(user.getFirstName(), user.getLastName());
    }

    @Test
    void find_users_by_id_if_exists() {
        List<Long> ids = List.of(1L, 2L, 7L);

        List<User> users = ids.stream()
                .map(this::findById)
                .flatMap(Optional::stream)
                .toList();
        int expectedNumOfFoundUsers = 2;

        assertEquals(expectedNumOfFoundUsers, users.size());
    }

    @Test
    void traditional_find_users_by_id_if_exists() {
        List<Long> ids = List.of(1L, 2L, 7L);

        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            Optional<User> optionalUser = findById(id);
            if (optionalUser.isPresent()) {
                users.add(optionalUser.get());
            }
        }

        int expectedNumOfFoundUsers = 2;

        assertEquals(expectedNumOfFoundUsers, users.size());
    }

    private Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
}
