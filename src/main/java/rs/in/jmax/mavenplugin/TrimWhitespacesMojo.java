package rs.in.jmax.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "trim-whitespaces", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TrimWhitespacesMojo extends AbstractMojo {

    private static final String[] EXTENSIONS = {"java", "xml"};

    private Log logger = getLog();

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    public void execute() throws MojoExecutionException, MojoFailureException {

        logger.info("TrimWhitespaces::");

        Collection<File> matchingFiles = FileUtils.listFiles(basedir, EXTENSIONS, true);

        for (File matchingFile : matchingFiles) {

            logger.debug("Reading file: " + matchingFile.getAbsolutePath());

            List<String> lines;
            try {
                lines = FileUtils.readLines(matchingFile, "UTF-8");
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to read lines from " + matchingFile.getAbsolutePath(), e);
            }

            Boolean isFileModified = false;
            List<String> trimmedLines = new ArrayList<String>(lines.size());
            int lineNumber = 0;

            for (String line : lines) {

                if (logger.isDebugEnabled()) {
                    lineNumber++;
                }

                // replace tab with spaces
                line = StringUtils.replace(line, "\\t", "    ");

                // strip end
                String trimmedLine = StringUtils.stripEnd(line, null);

                Boolean isLineModified = (!trimmedLine.equals(line));

                if (logger.isDebugEnabled()) {
                    if (isLineModified) {
                        logger.debug("Whitespace found on line " + lineNumber);
                    }
                }

                trimmedLines.add(trimmedLine);

                isFileModified = (isFileModified || isLineModified);
            }

            if (isFileModified) {
                try {
                    FileUtils.writeLines(matchingFile, "UTF-8", trimmedLines);
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to write lines to " + matchingFile.getAbsolutePath(), e);
                }
            }
        }
    }
}