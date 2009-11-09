package org.semanticdesktop.wiki.core.adapter.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.semanticdesktop.rdf.IRdfCursor;
import org.semanticdesktop.rdf.IRdfRepository;
import org.semanticdesktop.rdf.RdfException;
import org.semanticdesktop.rdf.id.IRdfId;
import org.semanticdesktop.rdf.id.IRdfIdService;
import org.semanticdesktop.rdf.query.IRdfQueryService;
import org.semanticdesktop.rdf.triple.IRdfTripleService;
import org.semanticdesktop.rdf.values.IRdfReferenceValue;
import org.semanticdesktop.rdf.values.IRdfValue;

/**
 * This class provides utility methods to create/update/delete indexes of rdf
 * statements.
 * 
 * @author kotelnikov
 */
public class TripleIndex {

    /**
     * The namespace used for wiki references
     */
    public final static String WIKI_NAMESPACE = "wiki:";

    /**
     * The namespace for the internal (anonymous) wiki nodes (embedded
     * documents)
     */
    public final static String WIKI_RANDOM_NAMESPACE = WIKI_NAMESPACE
        + "internal:";

    /**
     * Rdf repository used to index information
     */
    protected IRdfRepository fRepository;

    /**
     * The identifier of the graph
     */
    protected IRdfId fGraphId;

    /**
     * ID management RDF service
     */
    protected IRdfIdService fIdService;

    /**
     * The RDF query service
     */
    protected IRdfQueryService fQueryService;

    /**
     * "Triple" service (used to index RDF statements)
     */
    protected IRdfTripleService fTripleService;

    /**
     * The link translator used to transform wiki page links to the indexed uris
     * and vice versa.
     */
    public ILinkTranslator fLinkTranslator;

    /**
     * @param repository
     * @param linkTranslator
     * @throws RdfException
     */
    public TripleIndex(IRdfRepository repository, ILinkTranslator linkTranslator)
        throws RdfException {
        super();
        fLinkTranslator = linkTranslator;
        fRepository = repository;
        fIdService = (IRdfIdService) fRepository
            .getService(IRdfIdService.class);
        // graphId = getId(namespace);
        fGraphId = null;
        fTripleService = (IRdfTripleService) fRepository
            .getService(IRdfTripleService.class);
        fQueryService = (IRdfQueryService) fRepository
            .getService(IRdfQueryService.class);
    }

    /**
     * Indexes all references with the given subject and predicate
     * 
     * @param subjectUri the subject uri of the statement to index
     * @param predicateUri the predicate uri of the statement to index
     * @param objectUris the object uri of the statement to index
     * @throws RdfException
     */
    public void addStatements(
        String subjectUri,
        String predicateUri,
        String[] objectUris) throws RdfException {
        IRdfId subjectId = getId(subjectUri);
        IRdfId predicateId = getId(predicateUri);
        for (int j = 0; j < objectUris.length; j++) {
            //:SL:
        	if (objectUris[j] == null)
        		continue;
        	IRdfId objectId = getId(objectUris[j]);
            
            fTripleService.addStatement(
                fGraphId,
                subjectId,
                predicateId,
                objectId);
        }
    }

    /**
     * Retursn a list containing all resulting values (serialized as strings)
     * specified by the given SPARQL query.
     * 
     * @param query the SPARQL query to execute
     * @return a result of execution of the given query; all returned values are
     *         serialized in strings
     * @throws RdfException
     */
    public List<List<String>> executeQuery(String query) throws RdfException {
        List<List<String>> list = new ArrayList<List<String>>();
        IRdfCursor<IRdfValue[]> cursor = fQueryService.executeQuery(query);
        try {
            while (cursor.loadNextRow()) {
                IRdfValue[] values = cursor.getCurrentRow();
                List<String> row = new ArrayList<String>();
                list.add(row);
                if (values != null) {
                    for (IRdfValue value : values) {
                        if (value instanceof IRdfReferenceValue) {
                            IRdfReferenceValue ref = (IRdfReferenceValue) value;
                            String link = getLink(ref.getRdfId());
                            row.add(link);
                        } else {
                            row.add((value != null) ? value.toString() : null);
                        }
                    }
                }
            }
            return list;
        } finally {
            cursor.close();
        }
    }

    /**
     * Creates and returns a new uri.
     * 
     * @return a newly created uri.
     */
    public String generateNewUri() {
        UUID id = UUID.randomUUID();
        return WIKI_RANDOM_NAMESPACE + id.toString();
    }

    /**
     * @param uri the uri to transform to an id
     * @return an rdf identifier corresponding to the given uri
     * @throws RdfException
     */
    protected IRdfId getId(String uri) throws RdfException {
        if (uri == null)
            return null;
        uri = fLinkTranslator.getUriFromLink(uri);
        return fIdService.getRdfId(uri);
    }

    private String getLink(IRdfId id) throws RdfException {
        return fLinkTranslator.getLinkFromUri(id.getURI());
    }

    /**
     * @param cursor this cursor returns rdf statements
     * @return a list of reference statements (subjectUri, predicateUri,
     *         objectUri) from the given cursor
     * @throws RdfException
     */
    private List<List<String>> getStatementList(IRdfCursor<IRdfValue[]> cursor)
        throws RdfException {
        List<List<String>> list = new ArrayList<List<String>>();
        try {
            while (cursor.loadNextRow()) {
                IRdfValue[] value = cursor.getCurrentRow();
                IRdfId localSubjectId = ((IRdfReferenceValue) value[0])
                    .getRdfId();
                IRdfId predicateId = ((IRdfReferenceValue) value[1]).getRdfId();
                IRdfId objectId = ((IRdfReferenceValue) value[2]).getRdfId();
                String subjectUri = getLink(localSubjectId);
                String predicateUri = getLink(predicateId);
                String objectUri = getLink(objectId);
                List<String> row = new ArrayList<String>();
                row.add(subjectUri);
                row.add(predicateUri);
                row.add(objectUri);
                list.add(row);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * Returns a list of statements containing the specified subject, predicate
     * and object.
     * 
     * @param subjectUri the uri of the subject; can be <code>null</code>
     * @param predicateUri the uri of the predicate; can be <code>null</code>
     * @param objectUri the uri of the object; can be <code>null</code>
     * @return a list of all statements containing the specified subject,
     *         predicate and object
     * @throws RdfException
     */
    public List<List<String>> getStatements(
        String subjectUri,
        String predicateUri,
        String objectUri) throws RdfException {
        IRdfId subjectId = getId(subjectUri);
        IRdfId predicateId = getId(predicateUri);
        IRdfId objectId = getId(objectUri);
        IRdfCursor<IRdfValue[]> cursor = fTripleService.getStatements(
            fGraphId,
            subjectId,
            predicateId,
            objectId);
        List<List<String>> list = getStatementList(cursor);
        return list;
    }

    /**
     * Deletes all statements with the given subject, predicate and object.
     * 
     * @param subjectUri the uri of the subjects of statements to delete
     * @param predicateUri the uri of the predicates of statements to delete
     * @param objectUri the uri of the objects of statements to delete
     * @throws RdfException
     */
    public void removeStatements(
        String subjectUri,
        String predicateUri,
        String objectUri) throws RdfException {
        IRdfId subjectId = getId(subjectUri);
        IRdfId predicateId = getId(predicateUri);
        IRdfId objectId = getId(objectUri);
        fTripleService.removeStatement(
            fGraphId,
            subjectId,
            predicateId,
            objectId);
    }

}
