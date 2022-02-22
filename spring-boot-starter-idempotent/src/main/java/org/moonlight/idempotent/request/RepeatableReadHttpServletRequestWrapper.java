package org.moonlight.idempotent.request;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 重新包装HttpServletRequestWrapper使其可重复读
 * @author Moonlight
 */
public class RepeatableReadHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    private BufferedReader reader;
    private ServletInputStream inputStream;

    public RepeatableReadHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try (ServletInputStream in = request.getInputStream()) {
            body = IOUtils.toByteArray(in);
            inputStream = new RequestCachingInputStream(body);
        }
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
        }
        return reader;
    }

    private static class RequestCachingInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public synchronized void reset() throws IOException {
            inputStream.reset();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener) { }
    }

}
