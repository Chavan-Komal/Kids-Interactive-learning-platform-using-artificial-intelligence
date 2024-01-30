package com.aiplatform.aiplatform;



import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;

public final class DigitClassifier {
    private Interpreter interpreter;
    private boolean isInitialized;
    private final ExecutorService executorService;
    private int inputImageWidth;
    private int inputImageHeight;
    private int modelInputSize;
    private final Context context;
    private static final String TAG = "DigitClassifier";
    private static final int FLOAT_TYPE_SIZE = 4;
    private static final int PIXEL_SIZE = 1;
    private static final int OUTPUT_CLASSES_COUNT = 10;
    @NotNull
    public static final DigitClassifier.Companion Companion = new DigitClassifier.Companion((DefaultConstructorMarker)null);

    public final boolean isInitialized() {
        return this.isInitialized;
    }

    @NotNull
    public final Task initialize() {
        final TaskCompletionSource task = new TaskCompletionSource();
        this.executorService.execute((Runnable)(new Runnable() {
            public final void run() {
                try {
                    DigitClassifier.this.initializeInterpreter();
                    task.setResult((Object)null);
                } catch (IOException var2) {
                    task.setException((Exception)var2);
                }

            }
        }));
        Task var10000 = task.getTask();
        Intrinsics.checkNotNullExpressionValue(var10000, "task.task");
        return var10000;
    }

    private final void initializeInterpreter() throws IOException {
        AssetManager assetManager = this.context.getAssets();
        Intrinsics.checkNotNullExpressionValue(assetManager, "assetManager");
        ByteBuffer model = this.loadModelFile(assetManager, "mnist.tflite");
        Interpreter interpreter = new Interpreter(model);
        int[] inputShape = interpreter.getInputTensor(0).shape();
        this.inputImageWidth = inputShape[1];
        this.inputImageHeight = inputShape[2];
        this.modelInputSize = 4 * this.inputImageWidth * this.inputImageHeight * 1;
        this.interpreter = interpreter;
        this.isInitialized = true;
        Log.d("DigitClassifier", "Initialized TFLite interpreter.");
    }

    private final ByteBuffer loadModelFile(AssetManager assetManager, String filename) throws IOException {
        AssetFileDescriptor var10000 = assetManager.openFd(filename);
        Intrinsics.checkNotNullExpressionValue(var10000, "assetManager.openFd(filename)");
        AssetFileDescriptor fileDescriptor = var10000;
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer var10 = fileChannel.map(MapMode.READ_ONLY, startOffset, declaredLength);
        Intrinsics.checkNotNullExpressionValue(var10, "fileChannel.map(FileChan…rtOffset, declaredLength)");
        return (ByteBuffer)var10;
    }

    private final String classify(Bitmap bitmap) throws Throwable {
        boolean var2 = this.isInitialized;
        if (!var2) {
            boolean var21 = false;
            String var22 = "TF Lite Interpreter is not initialized yet.";
            throw (Throwable)(new IllegalStateException(var22.toString()));
        } else {
            Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, this.inputImageWidth, this.inputImageHeight, true);
            Intrinsics.checkNotNullExpressionValue(resizedImage, "resizedImage");
            ByteBuffer byteBuffer = this.convertBitmapToByteBuffer(resizedImage);
            byte var5 = 1;
            float[][] var6 = new float[var5][];

            for(int var7 = 0; var7 < var5; ++var7) {
                boolean var9 = false;
                float[] var19 = new float[10];
                var6[var7] = var19;
            }

            float[][] output = (float[][])var6;
            Interpreter var10000 = this.interpreter;
            if (var10000 != null) {
                var10000.run(byteBuffer, output);
            }

            float[] result = output[0];
            Iterable $this$maxBy$iv = (Iterable)ArraysKt.getIndices(result);
            boolean $i$f$maxBy = false;
            boolean $i$f$maxByOrNull = false;
            Iterator iterator$iv$iv = $this$maxBy$iv.iterator();
            Object var32;
            if (!iterator$iv$iv.hasNext()) {
                var32 = null;
            } else {
                Object maxElem$iv$iv = iterator$iv$iv.next();
                if (!iterator$iv$iv.hasNext()) {
                    var32 = maxElem$iv$iv;
                } else {
                    int it = ((Number)maxElem$iv$iv).intValue();
                    boolean var14 = false;
                    float maxValue$iv$iv = result[it];

                    do {
                        Object e$iv$iv = iterator$iv$iv.next();
                         it = ((Number)e$iv$iv).intValue();
                        boolean var16 = false;
                        float v$iv$iv = result[it];
                        if (Float.compare(maxValue$iv$iv, v$iv$iv) < 0) {
                            maxElem$iv$iv = e$iv$iv;
                            maxValue$iv$iv = v$iv$iv;
                        }
                    } while(iterator$iv$iv.hasNext());

                    var32 = maxElem$iv$iv;
                }
            }

            int maxIndex = (Integer)var32 != null ? (Integer)var32 : -1;
            String var27 = "Prediction Result: %d\nConfidence: %2f";
            Object[] var28 = new Object[]{maxIndex, result[maxIndex]};
            String var33 = String.format(var27, Arrays.copyOf(var28, var28.length));
            Intrinsics.checkNotNullExpressionValue(var33, "java.lang.String.format(this, *args)");
            String resultString = var33;
            return resultString;
        }
    }

    @NotNull
    public final Task classifyAsync(@NotNull final Bitmap bitmap) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        final TaskCompletionSource task = new TaskCompletionSource();
        this.executorService.execute((Runnable)(new Runnable() {
            public final void run() {
                String result = null;
                try {
                    result = DigitClassifier.this.classify(bitmap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                task.setResult(result);
            }
        }));
        Task var10000 = task.getTask();
        Intrinsics.checkNotNullExpressionValue(var10000, "task.task");
        return var10000;
    }

    public final void close() {
        this.executorService.execute((Runnable)(new Runnable() {
            public final void run() {
                Interpreter var10000 = DigitClassifier.this.interpreter;
                if (var10000 != null) {
                    var10000.close();
                }

                Log.d("DigitClassifier", "Closed TFLite interpreter.");
            }
        }));
    }

    private final ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.modelInputSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[this.inputImageWidth * this.inputImageHeight];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] var6 = pixels;
        int var7 = pixels.length;

        for(int var5 = 0; var5 < var7; ++var5) {
            int pixelValue = var6[var5];
            int r = pixelValue >> 16 & 255;
            int g = pixelValue >> 8 & 255;
            int b = pixelValue & 255;
            float normalizedPixelValue = (float)(r + g + b) / 3.0F / 255.0F;
            byteBuffer.putFloat(normalizedPixelValue);
        }

        Intrinsics.checkNotNullExpressionValue(byteBuffer, "byteBuffer");
        return byteBuffer;
    }

    public DigitClassifier(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        //super();
        this.context = context;
        ExecutorService var10001 = Executors.newCachedThreadPool();
        Intrinsics.checkNotNullExpressionValue(var10001, "Executors.newCachedThreadPool()");
        this.executorService = var10001;
    }

    // $FF: synthetic method
    public static final void access$setInterpreter$p(DigitClassifier $this, Interpreter var1) {
        $this.interpreter = var1;
    }


    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}