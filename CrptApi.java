import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private static final String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private Semaphore requestSemaphore;
    private TimeUnit timeUnit;
    private int requestLimit;
    private ObjectMapper mapper;
    private RateLimiter rateLimiter;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestSemaphore = new Semaphore(requestLimit);
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.mapper = new ObjectMapper();
        this.rateLimiter = new RateLimiter(timeUnit, requestLimit);

        Thread thread = new Thread(new RateLimiterController(rateLimiter, timeUnit));
        thread.start();
    }

    public void createDoc(Document document, String signature){
        try {
            rateLimiter.acquire();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .headers("signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(document)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private EDocType doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;

        private Date production_date;
        private String production_type;
        private List<Product> products;

        private Date reg_date;
        private String reg_number;

        public Document(Description description, String doc_id, String doc_status, EDocType doc_type, boolean importRequest, String owner_inn, String participant_inn, String producer_inn, Date production_date, String production_type, List<Product> products, Date reg_date, String reg_number) {
            this.description = description;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public EDocType getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(EDocType doc_type) {
            this.doc_type = doc_type;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public void setProduction_date(Date production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public Date getReg_date() {
            return reg_date;
        }

        public void setReg_date(Date reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }
    }

    private class Description {
        private String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }
    }

    private class Product {
        private String certificate_document;
        private Date certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private Date production_date;

        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public Product(String certificate_document, Date certificate_document_date, String certificate_document_number, String owner_inn, String producer_inn, Date production_date, String tnved_code, String uit_code, String uitu_code) {
            this.certificate_document = certificate_document;
            this.certificate_document_date = certificate_document_date;
            this.certificate_document_number = certificate_document_number;
            this.owner_inn = owner_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.tnved_code = tnved_code;
            this.uit_code = uit_code;
            this.uitu_code = uitu_code;
        }

        public String getCertificate_document() {
            return certificate_document;
        }

        public void setCertificate_document(String certificate_document) {
            this.certificate_document = certificate_document;
        }

        public Date getCertificate_document_date() {
            return certificate_document_date;
        }

        public void setCertificate_document_date(Date certificate_document_date) {
            this.certificate_document_date = certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public void setCertificate_document_number(String certificate_document_number) {
            this.certificate_document_number = certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public void setProduction_date(Date production_date) {
            this.production_date = production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public void setTnved_code(String tnved_code) {
            this.tnved_code = tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public void setUit_code(String uit_code) {
            this.uit_code = uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }

        public void setUitu_code(String uitu_code) {
            this.uitu_code = uitu_code;
        }
    }

    private enum EDocType {
        LP_INTRODUCE_GOODS,
        LP_INTRODUCE_GOODS_CSV,
        LP_INTRODUCE_GOODS_XML
    }

    private class RateLimiter {
        private Queue<Long> requestTime;
        private TimeUnit timeUnit;
        private int requestLimit;

        public RateLimiter(TimeUnit timeUnit, int requestLimit) {
            this.requestTime = new LinkedList<>();
            this.timeUnit = timeUnit;
            this.requestLimit = requestLimit;
        }

        public synchronized void acquire() {
            while (true) {
                if(!isFree()) {
                    try {
                        Long time = this.requestTime.peek();

                        if(time != null && !isFree()) {
                            long sleepTime = time + this.timeUnit.toMillis(1) - System.currentTimeMillis();

                            if(sleepTime > 0) {
                                Thread.sleep(sleepTime);
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    this.requestTime.add(System.currentTimeMillis());
                }
            }
        }
        public void release() {
            this.requestTime.poll();
        }

        private boolean isFree() {
            return this.requestTime.size() < this.requestLimit;
        }

        public Queue<Long> getRequestTime() {
            return this.requestTime;
        }
    }

    private class RateLimiterController implements Runnable {

        private final RateLimiter limiter;
        private final TimeUnit timeUnit;

        private RateLimiterController(RateLimiter limiter, TimeUnit timeUnit) {
            this.limiter = limiter;
            this.timeUnit = timeUnit;
        }

        @Override
        public void run() {

            while (true) {
                Long time = this.limiter.getRequestTime().peek();

                if(time != null) {
                    long unlockTime = time + this.timeUnit.toMillis(1);

                    if(System.currentTimeMillis() > unlockTime) {
                        this.limiter.release();
                    }
                    else {
                        long sleepTime = unlockTime - System.currentTimeMillis();

                        if(sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                else {
                    try {
                        Thread.sleep(2000);

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }
}
