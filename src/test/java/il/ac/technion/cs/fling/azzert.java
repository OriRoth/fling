package il.ac.technion.cs.fling;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;
import org.assertj.core.api.*;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
public class azzert extends AutoCloseableSoftAssertions {
  public void all() {
    super.assertAll();
  }
  public void also(AssertionErrorCollector collector) {
    super.assertAlso(collector);
  }
  public void collect(AssertionError error) {
    super.collectAssertionError(error);
  }
  public boolean success() {
    return super.wasSuccess();
  }
  public AtomicBooleanAssert that(AtomicBoolean actual) {
    return super.assertThat(actual);
  }
  public AtomicIntegerAssert that(AtomicInteger actual) {
    return super.assertThat(actual);
  }
  public AtomicIntegerArrayAssert that(AtomicIntegerArray actual) {
    return super.assertThat(actual);
  }
  public <OBJECT> AtomicIntegerFieldUpdaterAssert<OBJECT> that(AtomicIntegerFieldUpdater<OBJECT> actual) {
    return super.assertThat(actual);
  }
  public AtomicLongAssert that(AtomicLong actual) {
    return super.assertThat(actual);
  }
  public AtomicLongArrayAssert that(AtomicLongArray actual) {
    return super.assertThat(actual);
  }
  public <OBJECT> AtomicLongFieldUpdaterAssert<OBJECT> that(AtomicLongFieldUpdater<OBJECT> actual) {
    return super.assertThat(actual);
  }
  public <VALUE> AtomicMarkableReferenceAssert<VALUE> that(AtomicMarkableReference<VALUE> actual) {
    return super.assertThat(actual);
  }
  public <VALUE> AtomicReferenceAssert<VALUE> that(AtomicReference<VALUE> actual) {
    return super.assertThat(actual);
  }
  public <ELEMENT> AtomicReferenceArrayAssert<ELEMENT> that(AtomicReferenceArray<ELEMENT> actual) {
    return super.assertThat(actual);
  }
  public <FIELD, OBJECT> AtomicReferenceFieldUpdaterAssert<FIELD, OBJECT> that(
      AtomicReferenceFieldUpdater<OBJECT, FIELD> actual) {
    return super.assertThat(actual);
  }
  public <VALUE> AtomicStampedReferenceAssert<VALUE> that(AtomicStampedReference<VALUE> actual) {
    return super.assertThat(actual);
  }
  public BigDecimalAssert that(BigDecimal actual) {
    return super.assertThat(actual);
  }
  public BigIntegerAssert that(BigInteger actual) {
    return super.assertThat(actual);
  }
  public BooleanAssert that(boolean actual) {
    return super.assertThat(actual);
  }
  public BooleanAssert that(Boolean actual) {
    return super.assertThat(actual);
  }
  public BooleanArrayAssert that(boolean[] actual) {
    return super.assertThat(actual);
  }
  public ByteAssert that(byte actual) {
    return super.assertThat(actual);
  }
  public ByteAssert that(Byte actual) {
    return super.assertThat(actual);
  }
  public ByteArrayAssert that(byte[] actual) {
    return super.assertThat(actual);
  }
  public CharacterAssert that(char actual) {
    return super.assertThat(actual);
  }
  public CharArrayAssert that(char[] actual) {
    return super.assertThat(actual);
  }
  public CharacterAssert that(Character actual) {
    return super.assertThat(actual);
  }
  public CharSequenceAssert that(CharSequence actual) {
    return super.assertThat(actual);
  }
  public ProxyableClassAssert that(Class<?> actual) {
    return super.assertThat(actual);
  }
  public <RESULT> CompletableFutureAssert<RESULT> that(CompletableFuture<RESULT> actual) {
    return super.assertThat(actual);
  }
  public <RESULT> CompletableFutureAssert<RESULT> that(CompletionStage<RESULT> actual) {
    return super.assertThat(actual);
  }
  public DateAssert that(Date actual) {
    return super.assertThat(actual);
  }
  public DoubleAssert that(double actual) {
    return super.assertThat(actual);
  }
  public DoubleAssert that(Double actual) {
    return super.assertThat(actual);
  }
  public DoubleArrayAssert that(double[] actual) {
    return super.assertThat(actual);
  }
  public DoublePredicateAssert that(DoublePredicate actual) {
    return super.assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Double>, Double, ObjectAssert<Double>> that(DoubleStream actual) {
    return super.assertThat(actual);
  }
  public DurationAssert that(Duration actual) {
    return super.assertThat(actual);
  }
  public FileAssert that(File actual) {
    return super.assertThat(actual);
  }
  public FloatAssert that(float actual) {
    return super.assertThat(actual);
  }
  public FloatAssert that(Float actual) {
    return super.assertThat(actual);
  }
  public FloatArrayAssert that(float[] actual) {
    return super.assertThat(actual);
  }
  public <RESULT> FutureAssert<RESULT> that(Future<RESULT> actual) {
    return super.assertThat(actual);
  }
  public InputStreamAssert that(InputStream actual) {
    return super.assertThat(actual);
  }
  public InstantAssert that(Instant actual) {
    return super.assertThat(actual);
  }
  public IntegerAssert that(int actual) {
    return super.assertThat(actual);
  }
  public IntArrayAssert that(int[] actual) {
    return super.assertThat(actual);
  }
  public IntegerAssert that(Integer actual) {
    return super.assertThat(actual);
  }
  public IntPredicateAssert that(IntPredicate actual) {
    return super.assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Integer>, Integer, ObjectAssert<Integer>> that(IntStream actual) {
    return super.assertThat(actual);
  }
  public <T> ProxyableIterableAssert<T> that(Iterable<? extends T> actual) {
    return super.assertThat(actual);
  }
  public <T> IteratorAssert<T> that(Iterator<? extends T> actual) {
    return super.assertThat(actual);
  }
  public <T> ProxyableListAssert<T> that(List<? extends T> actual) {
    return super.assertThat(actual);
  }
  public LocalDateAssert that(LocalDate actual) {
    return super.assertThat(actual);
  }
  public LocalDateTimeAssert that(LocalDateTime actual) {
    return super.assertThat(actual);
  }
  public LocalTimeAssert that(LocalTime actual) {
    return super.assertThat(actual);
  }
  public LongAssert that(long actual) {
    return super.assertThat(actual);
  }
  public LongAssert that(Long actual) {
    return super.assertThat(actual);
  }
  public LongArrayAssert that(long[] actual) {
    return super.assertThat(actual);
  }
  public LongAdderAssert that(LongAdder actual) {
    return super.assertThat(actual);
  }
  public LongPredicateAssert that(LongPredicate actual) {
    return super.assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Long>, Long, ObjectAssert<Long>> that(LongStream actual) {
    return super.assertThat(actual);
  }
  public <K, V> ProxyableMapAssert<K, V> that(Map<K, V> actual) {
    return super.assertThat(actual);
  }
  public OffsetDateTimeAssert that(OffsetDateTime actual) {
    return super.assertThat(actual);
  }
  public OffsetTimeAssert that(OffsetTime actual) {
    return super.assertThat(actual);
  }
  public <VALUE> OptionalAssert<VALUE> that(Optional<VALUE> actual) {
    return super.assertThat(actual);
  }
  public OptionalDoubleAssert that(OptionalDouble actual) {
    return super.assertThat(actual);
  }
  public OptionalIntAssert that(OptionalInt actual) {
    return super.assertThat(actual);
  }
  public OptionalLongAssert that(OptionalLong actual) {
    return super.assertThat(actual);
  }
  public PathAssert that(Path actual) {
    return super.assertThat(actual);
  }
  public <T> ProxyablePredicateAssert<T> that(Predicate<T> actual) {
    return super.assertThat(actual);
  }
  public ShortAssert that(short actual) {
    return super.assertThat(actual);
  }
  public ShortAssert that(Short actual) {
    return super.assertThat(actual);
  }
  public ShortArrayAssert that(short[] actual) {
    return super.assertThat(actual);
  }
  public <ELEMENT> SpliteratorAssert<ELEMENT> that(Spliterator<ELEMENT> actual) {
    return super.assertThat(actual);
  }
  public <ELEMENT> AbstractListAssert<?, List<? extends ELEMENT>, ELEMENT, ObjectAssert<ELEMENT>> that(
      Stream<? extends ELEMENT> actual) {
    return super.assertThat(actual);
  }
  public StringAssert that(String actual) {
    return super.assertThat(actual);
  }
  public CharSequenceAssert that(StringBuffer actual) {
    return super.assertThat(actual);
  }
  public CharSequenceAssert that(StringBuilder actual) {
    return super.assertThat(actual);
  }
  public <T extends Comparable<? super T>> AbstractComparableAssert<?, T> that(T actual) {
    return super.assertThat(actual);
  }
  public <T> ProxyableObjectAssert<T> that(T actual) {
    return super.assertThat(actual);
  }
  public <T> ProxyableObjectArrayAssert<T> that(T[] actual) {
    return super.assertThat(actual);
  }
  public ThrowableAssert that(Throwable actual) {
    return super.assertThat(actual);
  }
  public UriAssert that(URI actual) {
    return super.assertThat(actual);
  }
  public AbstractUrlAssert<?> that(URL actual) {
    return super.assertThat(actual);
  }
  public ZonedDateTimeAssert that(ZonedDateTime actual) {
    return super.assertThat(actual);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatCode(ThrowingCallable shouldRaiseOrNotThrowable) {
    return super.assertThatCode(shouldRaiseOrNotThrowable);
  }
  public <T> ProxyableObjectAssert<T> thatObject(T actual) {
    return super.assertThatObject(actual);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatThrownBy(ThrowingCallable shouldRaiseThrowable) {
    return super.assertThatThrownBy(shouldRaiseThrowable);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatThrownBy(ThrowingCallable shouldRaiseThrowable,
      String description, Object... args) {
    return super.assertThatThrownBy(shouldRaiseThrowable, description, args);
  }
}