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
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") public class azzert extends AutoCloseableSoftAssertions {
  public void all() {
    assertAll();
  }
  public void also(final AssertionErrorCollector collector) {
    assertAlso(collector);
  }
  public void collect(final AssertionError error) {
    collectAssertionError(error);
  }
  public boolean success() {
    return wasSuccess();
  }
  public AtomicBooleanAssert that(final AtomicBoolean actual) {
    return assertThat(actual);
  }
  public AtomicIntegerAssert that(final AtomicInteger actual) {
    return assertThat(actual);
  }
  public AtomicIntegerArrayAssert that(final AtomicIntegerArray actual) {
    return assertThat(actual);
  }
  public <OBJECT> AtomicIntegerFieldUpdaterAssert<OBJECT> that(final AtomicIntegerFieldUpdater<OBJECT> actual) {
    return assertThat(actual);
  }
  public AtomicLongAssert that(final AtomicLong actual) {
    return assertThat(actual);
  }
  public AtomicLongArrayAssert that(final AtomicLongArray actual) {
    return assertThat(actual);
  }
  public <OBJECT> AtomicLongFieldUpdaterAssert<OBJECT> that(final AtomicLongFieldUpdater<OBJECT> actual) {
    return assertThat(actual);
  }
  public <VALUE> AtomicMarkableReferenceAssert<VALUE> that(final AtomicMarkableReference<VALUE> actual) {
    return assertThat(actual);
  }
  public <VALUE> AtomicReferenceAssert<VALUE> that(final AtomicReference<VALUE> actual) {
    return assertThat(actual);
  }
  public <ELEMENT> AtomicReferenceArrayAssert<ELEMENT> that(final AtomicReferenceArray<ELEMENT> actual) {
    return assertThat(actual);
  }
  public <FIELD, OBJECT> AtomicReferenceFieldUpdaterAssert<FIELD, OBJECT> that(
      final AtomicReferenceFieldUpdater<OBJECT, FIELD> actual) {
    return assertThat(actual);
  }
  public <VALUE> AtomicStampedReferenceAssert<VALUE> that(final AtomicStampedReference<VALUE> actual) {
    return assertThat(actual);
  }
  public BigDecimalAssert that(final BigDecimal actual) {
    return assertThat(actual);
  }
  public BigIntegerAssert that(final BigInteger actual) {
    return assertThat(actual);
  }
  public BooleanAssert that(final boolean actual) {
    return assertThat(actual);
  }
  public BooleanAssert that(final Boolean actual) {
    return assertThat(actual);
  }
  public BooleanArrayAssert that(final boolean[] actual) {
    return assertThat(actual);
  }
  public ByteAssert that(final byte actual) {
    return assertThat(actual);
  }
  public ByteAssert that(final Byte actual) {
    return assertThat(actual);
  }
  public ByteArrayAssert that(final byte[] actual) {
    return assertThat(actual);
  }
  public CharacterAssert that(final char actual) {
    return assertThat(actual);
  }
  public CharArrayAssert that(final char[] actual) {
    return assertThat(actual);
  }
  public CharacterAssert that(final Character actual) {
    return assertThat(actual);
  }
  public CharSequenceAssert that(final CharSequence actual) {
    return assertThat(actual);
  }
  public ProxyableClassAssert that(final Class<?> actual) {
    return assertThat(actual);
  }
  public <RESULT> CompletableFutureAssert<RESULT> that(final CompletableFuture<RESULT> actual) {
    return assertThat(actual);
  }
  public <RESULT> CompletableFutureAssert<RESULT> that(final CompletionStage<RESULT> actual) {
    return assertThat(actual);
  }
  @SuppressWarnings("UseOfObsoleteDateTimeApi") public DateAssert that(final Date actual) {
    return assertThat(actual);
  }
  public DoubleAssert that(final double actual) {
    return assertThat(actual);
  }
  public DoubleAssert that(final Double actual) {
    return assertThat(actual);
  }
  public DoubleArrayAssert that(final double[] actual) {
    return assertThat(actual);
  }
  public DoublePredicateAssert that(final DoublePredicate actual) {
    return assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Double>, Double, ObjectAssert<Double>> that(final DoubleStream actual) {
    return assertThat(actual);
  }
  public DurationAssert that(final Duration actual) {
    return assertThat(actual);
  }
  public FileAssert that(final File actual) {
    return assertThat(actual);
  }
  public FloatAssert that(final float actual) {
    return assertThat(actual);
  }
  public FloatAssert that(final Float actual) {
    return assertThat(actual);
  }
  public FloatArrayAssert that(final float[] actual) {
    return assertThat(actual);
  }
  public <RESULT> FutureAssert<RESULT> that(final Future<RESULT> actual) {
    return assertThat(actual);
  }
  public InputStreamAssert that(final InputStream actual) {
    return assertThat(actual);
  }
  public InstantAssert that(final Instant actual) {
    return assertThat(actual);
  }
  public IntegerAssert that(final int actual) {
    return assertThat(actual);
  }
  public IntArrayAssert that(final int[] actual) {
    return assertThat(actual);
  }
  public IntegerAssert that(final Integer actual) {
    return assertThat(actual);
  }
  public IntPredicateAssert that(final IntPredicate actual) {
    return assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Integer>, Integer, ObjectAssert<Integer>> that(final IntStream actual) {
    return assertThat(actual);
  }
  public <T> ProxyableIterableAssert<T> that(final Iterable<? extends T> actual) {
    return assertThat(actual);
  }
  public <T> IteratorAssert<T> that(final Iterator<? extends T> actual) {
    return assertThat(actual);
  }
  public <T> ProxyableListAssert<T> that(final List<? extends T> actual) {
    return assertThat(actual);
  }
  public LocalDateAssert that(final LocalDate actual) {
    return assertThat(actual);
  }
  public LocalDateTimeAssert that(final LocalDateTime actual) {
    return assertThat(actual);
  }
  public LocalTimeAssert that(final LocalTime actual) {
    return assertThat(actual);
  }
  public LongAssert that(final long actual) {
    return assertThat(actual);
  }
  public LongAssert that(final Long actual) {
    return assertThat(actual);
  }
  public LongArrayAssert that(final long[] actual) {
    return assertThat(actual);
  }
  public LongAdderAssert that(final LongAdder actual) {
    return assertThat(actual);
  }
  public LongPredicateAssert that(final LongPredicate actual) {
    return assertThat(actual);
  }
  public AbstractListAssert<?, List<? extends Long>, Long, ObjectAssert<Long>> that(final LongStream actual) {
    return assertThat(actual);
  }
  public <K, V> ProxyableMapAssert<K, V> that(final Map<K, V> actual) {
    return assertThat(actual);
  }
  public OffsetDateTimeAssert that(final OffsetDateTime actual) {
    return assertThat(actual);
  }
  public OffsetTimeAssert that(final OffsetTime actual) {
    return assertThat(actual);
  }
  public <VALUE> OptionalAssert<VALUE> that(final Optional<VALUE> actual) {
    return assertThat(actual);
  }
  public OptionalDoubleAssert that(final OptionalDouble actual) {
    return assertThat(actual);
  }
  public OptionalIntAssert that(final OptionalInt actual) {
    return assertThat(actual);
  }
  public OptionalLongAssert that(final OptionalLong actual) {
    return assertThat(actual);
  }
  public PathAssert that(final Path actual) {
    return assertThat(actual);
  }
  public <T> ProxyablePredicateAssert<T> that(final Predicate<T> actual) {
    return assertThat(actual);
  }
  public ShortAssert that(final short actual) {
    return assertThat(actual);
  }
  public ShortAssert that(final Short actual) {
    return assertThat(actual);
  }
  public ShortArrayAssert that(final short[] actual) {
    return assertThat(actual);
  }
  public <ELEMENT> SpliteratorAssert<ELEMENT> that(final Spliterator<ELEMENT> actual) {
    return assertThat(actual);
  }
  public <ELEMENT> AbstractListAssert<?, List<? extends ELEMENT>, ELEMENT, ObjectAssert<ELEMENT>> that(
      final Stream<? extends ELEMENT> actual) {
    return assertThat(actual);
  }
  public StringAssert that(final String actual) {
    return assertThat(actual);
  }
  public CharSequenceAssert that(final StringBuffer actual) {
    return assertThat(actual);
  }
  public CharSequenceAssert that(final StringBuilder actual) {
    return assertThat(actual);
  }
  public <T extends Comparable<? super T>> AbstractComparableAssert<?, T> that(final T actual) {
    return assertThat(actual);
  }
  public <T> ProxyableObjectAssert<T> that(final T actual) {
    return assertThat(actual);
  }
  public <T> ProxyableObjectArrayAssert<T> that(final T[] actual) {
    return assertThat(actual);
  }
  public ThrowableAssert that(final Throwable actual) {
    return assertThat(actual);
  }
  public UriAssert that(final URI actual) {
    return assertThat(actual);
  }
  public AbstractUrlAssert<?> that(final URL actual) {
    return assertThat(actual);
  }
  public ZonedDateTimeAssert that(final ZonedDateTime actual) {
    return assertThat(actual);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatCode(final ThrowingCallable shouldRaiseOrNotThrowable) {
    return assertThatCode(shouldRaiseOrNotThrowable);
  }
  public <T> ProxyableObjectAssert<T> thatObject(final T actual) {
    return assertThatObject(actual);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatThrownBy(final ThrowingCallable shouldRaiseThrowable) {
    return assertThatThrownBy(shouldRaiseThrowable);
  }
  public AbstractThrowableAssert<?, ? extends Throwable> thatThrownBy(final ThrowingCallable shouldRaiseThrowable,
      final String description, final Object... args) {
    return assertThatThrownBy(shouldRaiseThrowable, description, args);
  }
}