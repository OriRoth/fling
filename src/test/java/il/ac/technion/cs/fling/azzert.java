package il.ac.technion.cs.fling;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.assertj.core.api.AbstractComparableAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.AbstractUrlAssert;
import org.assertj.core.api.AssertionErrorCollector;
import org.assertj.core.api.AtomicBooleanAssert;
import org.assertj.core.api.AtomicIntegerArrayAssert;
import org.assertj.core.api.AtomicIntegerAssert;
import org.assertj.core.api.AtomicIntegerFieldUpdaterAssert;
import org.assertj.core.api.AtomicLongArrayAssert;
import org.assertj.core.api.AtomicLongAssert;
import org.assertj.core.api.AtomicLongFieldUpdaterAssert;
import org.assertj.core.api.AtomicMarkableReferenceAssert;
import org.assertj.core.api.AtomicReferenceArrayAssert;
import org.assertj.core.api.AtomicReferenceAssert;
import org.assertj.core.api.AtomicReferenceFieldUpdaterAssert;
import org.assertj.core.api.AtomicStampedReferenceAssert;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.BigDecimalAssert;
import org.assertj.core.api.BigIntegerAssert;
import org.assertj.core.api.BooleanArrayAssert;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.ByteArrayAssert;
import org.assertj.core.api.ByteAssert;
import org.assertj.core.api.CharArrayAssert;
import org.assertj.core.api.CharSequenceAssert;
import org.assertj.core.api.CharacterAssert;
import org.assertj.core.api.CompletableFutureAssert;
import org.assertj.core.api.DateAssert;
import org.assertj.core.api.DoubleArrayAssert;
import org.assertj.core.api.DoubleAssert;
import org.assertj.core.api.DoublePredicateAssert;
import org.assertj.core.api.DurationAssert;
import org.assertj.core.api.FileAssert;
import org.assertj.core.api.FloatArrayAssert;
import org.assertj.core.api.FloatAssert;
import org.assertj.core.api.FutureAssert;
import org.assertj.core.api.InputStreamAssert;
import org.assertj.core.api.InstantAssert;
import org.assertj.core.api.IntArrayAssert;
import org.assertj.core.api.IntPredicateAssert;
import org.assertj.core.api.IntegerAssert;
import org.assertj.core.api.IteratorAssert;
import org.assertj.core.api.LocalDateAssert;
import org.assertj.core.api.LocalDateTimeAssert;
import org.assertj.core.api.LocalTimeAssert;
import org.assertj.core.api.LongAdderAssert;
import org.assertj.core.api.LongArrayAssert;
import org.assertj.core.api.LongAssert;
import org.assertj.core.api.LongPredicateAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.OffsetDateTimeAssert;
import org.assertj.core.api.OffsetTimeAssert;
import org.assertj.core.api.OptionalAssert;
import org.assertj.core.api.OptionalDoubleAssert;
import org.assertj.core.api.OptionalIntAssert;
import org.assertj.core.api.OptionalLongAssert;
import org.assertj.core.api.PathAssert;
import org.assertj.core.api.ProxyableClassAssert;
import org.assertj.core.api.ProxyableIterableAssert;
import org.assertj.core.api.ProxyableListAssert;
import org.assertj.core.api.ProxyableMapAssert;
import org.assertj.core.api.ProxyableObjectArrayAssert;
import org.assertj.core.api.ProxyableObjectAssert;
import org.assertj.core.api.ProxyablePredicateAssert;
import org.assertj.core.api.ShortArrayAssert;
import org.assertj.core.api.ShortAssert;
import org.assertj.core.api.SpliteratorAssert;
import org.assertj.core.api.StringAssert;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.UriAssert;
import org.assertj.core.api.ZonedDateTimeAssert;
class azzert extends AutoCloseableSoftAssertions {
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